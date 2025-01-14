package net.emilla.command.core;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaAppsException;
import net.emilla.util.Apps;
import net.emilla.util.Dialogs;

import java.util.List;

public abstract class CategoryCommand extends CoreCommand {

    private final int mAppCount;
    private Intent mLaunchIntent;
    private AlertDialog.Builder mAppChooser;

    protected CategoryCommand(AssistActivity act, CoreParams params,
            String category) {
        super(act, params);

        PackageManager pm = act.getPackageManager();
        List<ResolveInfo> appList = Apps.resolveList(pm, category);
        mAppCount = appList.size();
        if (mAppCount == 1) mLaunchIntent = Apps.launchIntent(appList.get(0).activityInfo);
        else if (mAppCount > 1) mAppChooser = Dialogs.appChooser(act, pm, appList);
    }

    @Override
    protected void run() {
        switch (mAppCount) {
        case 0 -> throw new EmlaAppsException(R.string.error, R.string.error_no_app);
        case 1 -> appSucceed(mLaunchIntent);
        default -> offerDialog(mAppChooser);
        // todo: allow to select a default app, ensuring that the preference is cleared if ever the default is no longer installed or a new candidate is installed
        // interestingly, Tasker is included if you remove CATEGORY_LAUNCHER from the intent. i assume this is for its special shortcut functionality.
        // will keep an eye on this. it shouldn't be included in this dialog (by default it "succeeds" to no actual activity, which was confusing to debug lol)
        // but it would be pretty useful to have a toolchain of those special launches ifwhen a dedicated "tasker" command is added.
        }
    }
}
