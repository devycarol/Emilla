package net.emilla.run;

import androidx.appcompat.app.AlertDialog;

import net.emilla.activity.AssistActivity;

public /*open*/ class DialogRun implements Runnable {

    private final AssistActivity mActivity;
    private final AlertDialog mDialog;

    public DialogRun(AssistActivity act, AlertDialog.Builder builder) {
        this(act, builder.create());
    }

    public DialogRun(AssistActivity act, AlertDialog dialog) {
        mActivity = act;
        dialog.setOnCancelListener(dlg -> {
            mActivity.onCloseDialog();
            mActivity.resume();
        });
        // Todo: don't require this
        mDialog = dialog;
    }

    @Override
    public final void run() {
        mActivity.prepareForDialog();
        mDialog.show();
    }
}
