package net.emilla.command.core;

import android.content.Intent;

import androidx.annotation.CallSuper;
import androidx.appcompat.app.AlertDialog;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.apps.AppList;
import net.emilla.apps.Apps;
import net.emilla.util.Dialogs;

public abstract class CategoryCommand extends CoreCommand {

    protected CategoryCommand(AssistActivity act, CoreEntry coreEntry, int imeAction) {
        super(act, coreEntry, imeAction);
    }

    private AppList mAppList = null;
    private AlertDialog.Builder mChooser = null;

    @Override @CallSuper
    protected void onInit() {
        super.onInit();

        if (mAppList == null) {
            mAppList = new AppList(pm(), makeFilter());
        }

        if (mAppList.size() > 1) {
            mChooser = Dialogs.appLaunches(this.activity, mAppList);
        }
    }

    protected abstract Intent makeFilter();

    @Override @CallSuper
    protected void onClean() {
        super.onClean();

        mAppList = null;
        mChooser = null;
    }

    @Override
    protected final void run() {
        switch (mAppList.size()) {
        case 0 -> throw badCommand(R.string.error_no_app);
        case 1 -> appSucceed(Apps.launchIntent(mAppList.get(0)));
        default -> offerDialog(mChooser);
        // todo: allow to select a default app, ensuring that the preference is cleared if ever the
        //  default is no longer installed or a new candidate is installed
        // interestingly, Tasker is included if you remove CATEGORY_LAUNCHER from the intent. i
        // assume this is for its special shortcut functionality. will keep an eye on this. it
        // shouldn't be included in this dialog (by default it "succeeds" to no actual activity,
        // which was confusing to debug lol) but it would be pretty useful to have a toolchain of
        // those special launches ifwhen a dedicated "tasker" command is added.
        }
    }
}
