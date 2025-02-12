package net.emilla.run;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.text.format.DateFormat;

import net.emilla.AssistActivity;

public final class TimePickerOffering implements Offering {

    private final AssistActivity mActivity;
    private final TimePickerDialog mDialog;

    public TimePickerOffering(AssistActivity act, OnTimeSetListener timeSet) {
        mActivity = act;
        mDialog = new TimePickerDialog(act, 0, timeSet, 12, 0, DateFormat.is24HourFormat(act));
        // TODO: this isn't respecting the LineageOS system 24-hour setting.
        // should there be an option for default time to be noon vs. the current time? noon seems
        // much more reasonable in all cases tbh. infinitely more predictable—who the heck wants to
        // set a timer for right now?!
        mDialog.setOnCancelListener(dlg -> {
            mActivity.onCloseDialog();
            mActivity.resume();
        });
        // Todo: don't require this.
    }

    @Override
    public void run() {
        mActivity.prepareForDialog();
        mDialog.show();
    }
}
