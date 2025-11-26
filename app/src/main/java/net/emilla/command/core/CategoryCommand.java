package net.emilla.command.core;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.command.app.AppEntry;
import net.emilla.run.AppSuccess;
import net.emilla.util.Apps;
import net.emilla.util.Dialogs;
import net.emilla.util.Intents;

import java.util.Arrays;

/*internal*/ abstract class CategoryCommand extends CoreCommand {

    private final AppEntry[] mApps;
    @Nullable
    private final AlertDialog.Builder mChooser;

    /*internal*/ CategoryCommand(AssistActivity act, CoreEntry coreEntry, int imeAction) {
        super(act, coreEntry, imeAction);

        mApps = Apps.filter(act.getPackageManager(), makeFilter());
        mChooser = mApps.length > 1
            ? appLaunches(act, mApps)
            : null;
    }

    private static AlertDialog.Builder appLaunches(AssistActivity act, AppEntry[] apps) {
        // TODO: include app icons in this dialog for better visual clarity and to disambiguate apps
        //  with duplicate names. a choice between a list and grid layout would be cool.
        String[] labels = Arrays.stream(apps)
            .map(app -> app.displayName)
            .toArray(String[]::new);
        return Dialogs.list(
            act,
            R.string.dialog_app,
            labels,
            (dlg, which) -> act.succeed(new AppSuccess(Intents.launchApp(apps[which])))
        );
    }

    protected abstract Intent makeFilter();

    @Override
    protected final void run(AssistActivity act) {
        switch (mApps.length) {
        case 0 -> throw badCommand(R.string.error_no_app);
        case 1 -> appSucceed(act, Intents.launchApp(mApps[0]));
        default -> offerDialog(act, mChooser);
        // todo: allow to select a default app, ensuring that the preference is cleared if ever the
        //  default is no longer installed or a new candidate is installed
        // interestingly, Tasker is included if you remove CATEGORY_LAUNCHER from the intent. i
        // assume this is for its special shortcut functionality. will keep an eye on this. it
        // shouldn't be included in this dialog (by default it "succeeds" to no actual activity,
        // which was confusing to debug lol) but it would be pretty useful to have a toolchain of
        // those special launches in the Tasker command.
        }
    }

}
