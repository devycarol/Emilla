package net.emilla.run;

import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;

public class DialogOffering extends DialogRun implements Offering {

    public DialogOffering(AssistActivity act, AlertDialog.Builder builder) {
        super(act, builder);
    }

    public DialogOffering(AssistActivity act, AlertDialog.Builder builder,
            DialogInterface.OnCancelListener cancel) {
        super(act, builder, cancel);
    }

    public DialogOffering(AssistActivity act, AlertDialog dialog) {
        super(act, dialog);
    }
}
