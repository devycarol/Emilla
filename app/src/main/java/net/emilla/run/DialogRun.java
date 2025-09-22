package net.emilla.run;

import androidx.appcompat.app.AlertDialog;

import net.emilla.activity.AssistActivity;

public /*open*/ class DialogRun implements CommandRun {

    protected final AlertDialog.Builder dialog;

    public DialogRun(AlertDialog.Builder dialog) {
        this.dialog = dialog;
    }

    @Override
    public /*open*/ void run(AssistActivity act) {
        dialog.setOnCancelListener(dlg -> {
            act.onCloseDialog(); // Todo: don't require this
            act.resume();
        });
        act.prepareForDialog();
        dialog.show();
    }
}
