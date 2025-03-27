package net.emilla.command.core;

import android.content.Intent;

import androidx.annotation.CallSuper;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.app.AppList;
import net.emilla.app.Apps;
import net.emilla.util.Dialogs;

public abstract class CategoryCommand extends CoreCommand {

    protected CategoryCommand(
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

    private AppList mAppList;
    private AlertDialog mChooser;

    @Override @CallSuper
    protected void onInit() {
        super.onInit();

        if (mAppList == null) mAppList = new AppList(pm(), makeFilter());
        if (mAppList.size() > 1) {
            mChooser = Dialogs.appLaunches(activity, mAppList).create();
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
