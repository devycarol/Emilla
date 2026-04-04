package net.emilla.command.core;

import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.command.EmillaCommand;
import net.emilla.command.app.AppEntry;
import net.emilla.run.AppSuccess;
import net.emilla.util.Apps;
import net.emilla.util.Dialogs;
import net.emilla.util.Intents;

import java.util.Arrays;

enum CategoryCommand {;
    public static void run(AssistActivity act, String category) {
        run(act, Intents.categoryTask(category));
    }

    public static void run(AssistActivity act, Intent filter) {
        AppEntry[] apps = Apps.filter(act.getPackageManager(), filter);

        switch (apps.length) {
        case 0 -> act.fail(R.string.error, R.string.error_no_app);
        case 1 -> Apps.succeed(act, Intents.launchApp(apps[0]));
        default -> EmillaCommand.offerDialog(act, appLaunches(act, apps));
        // todo: allow to select a default app, ensuring that the preference is cleared if ever the
        //  default is no longer installed or a new candidate is installed
        // interestingly, Tasker is included if you remove CATEGORY_LAUNCHER from the intent. i
        // assume this is for its special shortcut functionality. will keep an eye on this. it
        // shouldn't be included in this dialog (by default it "succeeds" to no actual activity,
        // which was confusing to debug lol) but it would be pretty useful to have a toolchain of
        // those special launches in the Tasker command.
        }
    }

    private static AlertDialog.Builder appLaunches(AssistActivity act, AppEntry[] apps) {
        // TODO: include app icons in this dialog for better visual clarity and to disambiguate apps
        //  with duplicate names. a choice between a list and grid layout would be cool.
        String[] labels = Arrays.stream(apps)
            .map(app -> app.displayName)
            .toArray(String[]::new)
        ;
        return Dialogs.list(
            act,
            R.string.dialog_app,
            labels,
            (dlg, which) -> act.succeed(AppSuccess.instance(Intents.launchApp(apps[which])))
        );
    }
}
