package net.emilla.command.core;

import android.content.Intent;
import android.content.res.Resources;

import androidx.appcompat.app.AlertDialog;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.util.AppList;
import net.emilla.util.Dialogs;
import net.emilla.util.Intents;

/*internal*/ abstract class CategoryCommand extends CoreCommand {

    /*internal*/ CategoryCommand(AssistActivity act, CoreEntry coreEntry, int imeAction) {
        super(act, coreEntry, imeAction);
    }

    private /*late*/ AppList mAppList;
    private AlertDialog.Builder mChooser = null;

    @Override
    protected final void init(AssistActivity act, Resources res) {
        super.init(act, res);

        mAppList = new AppList(pm(), makeFilter());
        if (mAppList.size() > 1) {
            mChooser = Dialogs.appLaunches(act, mAppList);
        }
    }

    protected abstract Intent makeFilter();

    @Override
    protected final void run() {
        switch (mAppList.size()) {
        case 0 -> throw badCommand(R.string.error_no_app);
        case 1 -> appSucceed(Intents.launchApp(mAppList.get(0)));
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
