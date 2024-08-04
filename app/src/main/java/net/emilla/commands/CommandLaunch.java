package net.emilla.commands;

import static java.util.Arrays.copyOfRange;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.DrawableRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.utils.Apps;
import net.emilla.utils.Dialogs;

import java.util.List;

public class CommandLaunch extends CoreCommand {
private final List<ResolveInfo> mAppList;
private final AlertDialog.Builder mAppChooser;

public CommandLaunch(final AssistActivity act) {
    super(act, R.string.command_launch, R.string.instruction_app);

    mAppList = act.appList();
    mAppChooser = Dialogs.appChooser(act, act.getPackageManager(), mAppList);
}

@Override
public Command cmd() {
    return Command.LAUNCH;
}

@Override @DrawableRes
public int icon() {
    return R.drawable.ic_launch;
}

@Override
public int imeAction() {
    return EditorInfo.IME_ACTION_GO;
}

@Override
public void run() {
    offer(mAppChooser.create());
}

@Override
public void run(final String app) {
    // todo: optimized pre-processed search
    final int appCount = mAppList.size();

    CharSequence[] prefLabels = new CharSequence[appCount];
    CharSequence[] otherLabels = new CharSequence[appCount];
    Intent[] prefIntents = new Intent[appCount];
    Intent[] otherIntents = new Intent[appCount];
    int prefCount = 0;
    int otherCount = 0;

    final PackageManager pm = packageManager();
    final String lcQuery = app.toLowerCase();
    boolean exactMatch = false;

    for (int i = 0; i < appCount; ++i) {
        ActivityInfo info = mAppList.get(i).activityInfo;

        CharSequence label = info.loadLabel(pm);
        String lcLabel = label.toString().toLowerCase();
        final String packageName = info.packageName;

        if (lcLabel.equals(lcQuery)) { // an exact match will drop all other results
            prefLabels = new CharSequence[appCount];
            prefIntents = new Intent[appCount];
            otherLabels = null;
            otherIntents = null;

            prefLabels[0] = label;
            prefIntents[0] = Apps.launchIntent(packageName, info.name);
            prefCount = 1;

            for (++i; i < appCount; ++i) { // continue searching for duplicates only
                info = mAppList.get(i).activityInfo;
                label = info.loadLabel(pm);
                lcLabel = label.toString().toLowerCase();

                if (lcLabel.equals(lcQuery)) {
                    prefLabels[prefCount] = label;
                    prefIntents[prefCount] = Apps.launchIntent(info.packageName, info.name);
                    ++prefCount;
                }
            }

            exactMatch = true;
            break; // search is finished
        }
        if (lcLabel.contains(lcQuery)) {
            final Intent in = Apps.launchIntent(packageName, info.name);
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
    case 0 -> throw new EmlaAppsException(resources().getString(R.string.error_no_apps));
    case 1 -> succeed(prefIntents[0]);
    default -> offer(Dialogs.withIntents(mAppChooser, activity(), copyOfRange(prefLabels, 0, prefCount), prefIntents).create());
}}
}
