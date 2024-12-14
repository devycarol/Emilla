package net.emilla.command.core;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.utils.Apps;
import net.emilla.utils.Dialogs;

import java.util.List;

public abstract class CategoryCommand extends CoreCommand {
private final int mAppCount;
private Intent mLaunchIntent;
private AlertDialog mAppChooser;

public CategoryCommand(AssistActivity act, String instruct, String category,
        @StringRes int nameId, @StringRes int instructionId) {
    super(act, instruct, nameId, instructionId);

    PackageManager pm = act.getPackageManager();
    List<ResolveInfo> appList = Apps.resolveList(pm, category);
    mAppCount = appList.size();
    if (mAppCount == 1) mLaunchIntent = Apps.launchIntent(appList.get(0).activityInfo);
    else if (mAppCount > 1) mAppChooser = Dialogs.appChooser(act, pm, appList).create();
}

protected abstract void noSuchApp(); // TODO: handle at mapping

@Override
protected void run() {
    switch (mAppCount) {
    case 0 -> noSuchApp();
    case 1 -> succeed(mLaunchIntent);
    default -> offer(mAppChooser);
    // todo: allow to select a default app, ensuring that the preference is cleared if ever the default is no longer installed or a new candidate is installed
    // interestingly, Tasker is included if you remove CATEGORY_LAUNCHER from the intent. i assume this is for its special shortcut functionality.
    // will keep an eye on this. it shouldn't be included in this dialog (by default it "succeeds" to no actual activity, which was confusing to debug lol)
    // but it would be pretty useful to have a toolchain of those special launches ifwhen a dedicated "tasker" command is added.
    }
}
}
