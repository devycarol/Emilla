package net.emilla.run;

import androidx.appcompat.app.AlertDialog;

import net.emilla.activity.AssistActivity;

public /*open*/ class DialogRun implements CommandRun {

    protected final AlertDialog.Builder pDialog;

    public DialogRun(AlertDialog.Builder dialog) {
        pDialog = dialog;
    }

    @Override
    public /*open*/ void run(AssistActivity act) {
        pDialog.setOnCancelListener(dlg -> {
            act.onCloseDialog(); // Todo: don't require this
            act.resume();
        });
        act.prepareForDialog();
        pDialog.show();
    }
}
