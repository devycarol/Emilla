package net.emilla.command.core;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import androidx.annotation.CallSuper;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.util.Dialogs;

import java.util.Arrays;
import java.util.List;

public abstract class OpenCommand extends CoreCommand {

    protected OpenCommand(
        AssistActivity act,
        @StringRes int name,
        @StringRes int instruction,
        @DrawableRes int icon,
        @StringRes int summary,
        @StringRes int manual,
        int imeAction
    ) {
        super(act, name,
              instruction,
              icon,
              summary,
              manual,
              imeAction);
    }

    protected AlertDialog.Builder appChooser;

    @Override @CallSuper
    protected void onInit() {
        super.onInit();
        appChooser = makeChooser();
    }

    @Override @CallSuper
    protected void onClean() {
        super.onClean();
        appChooser = null;
    }

    protected abstract AlertDialog.Builder makeChooser();

    protected final void appSearchRun(String app, IntentMaker action) {
        // todo: optimized pre-processed search
        List<ResolveInfo> appList = activity.appList();
        int appCount = appList.size();

        var prefLabels = new CharSequence[appCount];
        var otherLabels = new CharSequence[appCount];
        var prefIntents = new Intent[appCount];
        var otherIntents = new Intent[appCount];
        int prefCount = 0;
        int otherCount = 0;

        var lcQuery = app.toLowerCase();
        boolean exactMatch = false;

        PackageManager pm = pm();
        for (int i = 0; i < appCount; ++i) {
            ActivityInfo info = appList.get(i).activityInfo;

            CharSequence label = info.loadLabel(pm);
            var lcLabel = label.toString().toLowerCase();

            if (lcLabel.equals(lcQuery)) { // an exact match will drop all other results
                prefLabels = new CharSequence[appCount];
                prefIntents = new Intent[appCount];
                otherLabels = null;
                otherIntents = null;

                prefLabels[0] = label;
                prefIntents[0] = action.make(info.packageName, info.name);
                prefCount = 1;

                for (++i; i < appCount; ++i) { // continue searching for duplicates only
                    info = appList.get(i).activityInfo;
                    label = info.loadLabel(pm);
                    lcLabel = label.toString().toLowerCase();

                    if (lcLabel.equals(lcQuery)) {
                        prefLabels[prefCount] = label;
                        prefIntents[prefCount] = action.make(info.packageName, info.name);
                        ++prefCount;
                    }
                }

                exactMatch = true;
                break; // search is finished
            }
            if (lcLabel.contains(lcQuery)) {
                if (lcLabel.startsWith(lcQuery)) {
                    prefLabels[prefCount] = label;
                    prefIntents[prefCount] = action.make(info.packageName, info.name);
                    ++prefCount;
                } else {
                    otherLabels[otherCount] = label;
                    otherIntents[otherCount] = action.make(info.packageName, info.name);
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
                ++i; ++k;
            }
        } // else `other` arrays are null

        switch (prefCount) {
        case 0 -> throw badCommand(R.string.error_apps_not_found);
        // Todo: offer to search app store
        case 1 -> appSucceed(prefIntents[0]);
        default -> offerDialog(makeDialog(prefLabels, prefCount, prefIntents));
        }
    }

    @FunctionalInterface
    protected interface IntentMaker {
        Intent make(String pkg, String cls);
    }

    private AlertDialog.Builder makeDialog(
        CharSequence[] prefLabels,
        int prefCount,
        Intent[] prefIntents
    ) {
        CharSequence[] labels = Arrays.copyOfRange(prefLabels, 0, prefCount);
        return Dialogs.list(activity, R.string.dialog_app, labels,
                (dlg, which) -> appSucceed(prefIntents[which]));
    }
}
