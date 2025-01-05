package net.emilla.run;

import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;

public abstract class DialogRun implements CommandRun {

    private final AssistActivity mActivity;
    private final AlertDialog mDialog;

    public DialogRun(AssistActivity act, AlertDialog.Builder builder) {
        mActivity = act;
        mDialog = builder.setOnCancelListener(dlg -> mActivity.onCloseDialog()).create();
        // Todo: don't require this
    }

    @Override
    public void run() {
        mActivity.prepareForDialog();
        mDialog.show();
    }
}
