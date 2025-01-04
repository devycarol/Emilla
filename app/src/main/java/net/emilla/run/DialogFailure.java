package net.emilla.run;

import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;

public class DialogFailure extends DialogRun implements Failure {

    public DialogFailure(AssistActivity act, AlertDialog.Builder builder) {
        super(act, builder);
    }
}
