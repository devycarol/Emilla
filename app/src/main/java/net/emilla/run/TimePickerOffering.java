package net.emilla.run;

import android.app.TimePickerDialog;

import net.emilla.AssistActivity;

public class TimePickerOffering implements Offering {

    private final AssistActivity mActivity;
    private final TimePickerDialog mDialog;

    public TimePickerOffering(AssistActivity act, TimePickerDialog dialog) {
        mActivity = act;
        mDialog = dialog;
    }

    @Override
    public void run() {
        mActivity.prepareForDialog();
        mDialog.show();
    }
}
