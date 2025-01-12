package net.emilla.command.core;

import static java.util.Arrays.copyOfRange;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaAppsException;
import net.emilla.util.Dialogs;

import java.util.List;

public abstract class OpenCommand extends CoreCommand {

    protected final List<ResolveInfo> mAppList;
    protected final AlertDialog.Builder mAppChooser;

    protected OpenCommand(AssistActivity act, String instruct, CoreParams params) {
        super(act, instruct, params);

        mAppList = act.appList();
        mAppChooser = getAppChooser(act);
    }

    protected abstract AlertDialog.Builder getAppChooser(AssistActivity act);

    private AlertDialog.Builder getDialog(CharSequence[] prefLabels, int prefCount,
            Intent[] prefIntents) {
        return Dialogs.withIntents(mAppChooser, activity, copyOfRange(prefLabels, 0, prefCount), prefIntents);
    }

    @Override
    protected void run(String app) {
        // todo: optimized pre-processed search
        int appCount = mAppList.size();

        CharSequence[] prefLabels = new CharSequence[appCount];
        CharSequence[] otherLabels = new CharSequence[appCount];
        Intent[] prefIntents = new Intent[appCount];
        Intent[] otherIntents = new Intent[appCount];
        int prefCount = 0;
        int otherCount = 0;

        String lcQuery = app.toLowerCase();
        boolean exactMatch = false;

        for (int i = 0; i < appCount; ++i) {
            ActivityInfo info = mAppList.get(i).activityInfo;

            PackageManager pm = pm();
            CharSequence label = info.loadLabel(pm);
            String lcLabel = label.toString().toLowerCase();

            if (lcLabel.equals(lcQuery)) { // an exact match will drop all other results
                prefLabels = new CharSequence[appCount];
                prefIntents = new Intent[appCount];
                otherLabels = null;
                otherIntents = null;

                prefLabels[0] = label;
                prefIntents[0] = makeIntent(info.packageName, info.name);
                prefCount = 1;

                for (++i; i < appCount; ++i) { // continue searching for duplicates only
                    info = mAppList.get(i).activityInfo;
                    label = info.loadLabel(pm);
                    lcLabel = label.toString().toLowerCase();

                    if (lcLabel.equals(lcQuery)) {
                        prefLabels[prefCount] = label;
                        prefIntents[prefCount] = makeIntent(info.packageName, info.name);
                        ++prefCount;
                    }
                }

                exactMatch = true;
                break; // search is finished
            }
            if (lcLabel.contains(lcQuery)) {
                Intent in = makeIntent(info.packageName, info.name);
                if (lcLabel.startsWith(lcQuery)) {
                    prefLabels[prefCount] = label;
                    prefIntents[prefCount] = in;
                    ++prefCount;
                } else {
                    otherLabels[otherCount] = label;
                    otherIntents[otherCount] = in;
                    ++otherCount;
                }
            }
        }

        if (!exactMatch) {
            int i = prefCount, k = 0;
            prefCount += otherCount;
            while (i < prefCount) {
                prefLabels[i] = otherLabels[k];
                prefIntents[i] = otherIntents[k];
                ++i;
                ++k;
            }
        } // else `other` arrays are null

        switch (prefCount) {
        case 0 -> throw new EmlaAppsException(R.string.error, R.string.error_apps_not_found);
        // Todo: offer to search app store
        case 1 -> appSucceed(prefIntents[0]);
        default -> offerDialog(getDialog(prefLabels, prefCount, prefIntents));
    }}

    protected abstract Intent makeIntent(String pkg, String cls);
}
