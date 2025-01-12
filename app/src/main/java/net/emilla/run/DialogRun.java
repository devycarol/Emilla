package net.emilla.run;

import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;

public abstract class DialogRun implements CommandRun {

    private final AssistActivity mActivity;
    private final AlertDialog mDialog;

    public DialogRun(AssistActivity act, AlertDialog.Builder builder) {
        this(act, builder, dlg -> act.onCloseDialog());
        // Todo: don't require this
    }

    public DialogRun(AssistActivity act, AlertDialog.Builder builder,
            DialogInterface.OnCancelListener cancel) {
        mActivity = act;
        mDialog = builder.setOnCancelListener(cancel).create();
    }

    public DialogRun(AssistActivity act, AlertDialog dialog) {
        mActivity = act;
        dialog.setOnCancelListener(dlg -> mActivity.onCloseDialog());
        // Todo: don't require this
        mDialog = dialog;
    }

    @Override
    public void run() {
        mActivity.prepareForDialog();
        mDialog.show();
    }
}
