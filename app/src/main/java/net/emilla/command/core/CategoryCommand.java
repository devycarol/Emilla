package net.emilla.command.core;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.util.AppList;
import net.emilla.util.Dialogs;
import net.emilla.util.Intents;

/*internal*/ abstract class CategoryCommand extends CoreCommand {

    private final AppList mAppList;
    @Nullable
    private final AlertDialog.Builder mChooser;

    /*internal*/ CategoryCommand(AssistActivity act, CoreEntry coreEntry, int imeAction) {
        super(act, coreEntry, imeAction);

        mAppList = new AppList(act.getPackageManager(), makeFilter());
        mChooser = mAppList.size() > 1
            ? Dialogs.appLaunches(act, mAppList)
            : null;
    }

    protected abstract Intent makeFilter();

    @Override
    protected final void run(AssistActivity act) {
        switch (mAppList.size()) {
        case 0 -> throw badCommand(R.string.error_no_app);
        case 1 -> appSucceed(act, Intents.launchApp(mAppList.get(0)));
        default -> offerDialog(act, mChooser);
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
