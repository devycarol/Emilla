package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AlertDialog;

import net.emilla.activity.AssistActivity;
import net.emilla.apps.Apps;
import net.emilla.util.Dialogs;

/*internal*/ final class Launch extends OpenCommand {

    public static final String ENTRY = "launch";

    public static boolean possible() {
        return true;
    }

    /*internal*/ Launch(AssistActivity act) {
        super(act, CoreEntry.LAUNCH, EditorInfo.IME_ACTION_GO);
    }

    @Override
    protected void run() {
        offerDialog(this.appChooser);
    }

    @Override
    protected void run(String app) {
        appSearchRun(app, Apps::launchIntent);
    }

    @Override
    protected AlertDialog.Builder makeChooser() {
        return Dialogs.appLaunches(this.activity);
    }

}
