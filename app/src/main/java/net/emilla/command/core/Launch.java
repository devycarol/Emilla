package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AlertDialog;

import net.emilla.activity.AssistActivity;
import net.emilla.util.Dialogs;
import net.emilla.util.Intents;

/*internal*/ final class Launch extends OpenCommand {

    /*internal*/ Launch(AssistActivity act) {
        super(act, CoreEntry.LAUNCH, EditorInfo.IME_ACTION_GO);
    }

    @Override
    protected void run(AssistActivity act) {
        offerDialog(act, this.appChooser);
    }

    @Override
    protected void run(AssistActivity act, String app) {
        appSearchRun(act, app, Intents::launchApp);
    }

    @Override
    protected AlertDialog.Builder makeChooser(AssistActivity act) {
        return Dialogs.appLaunches(act);
    }

}
