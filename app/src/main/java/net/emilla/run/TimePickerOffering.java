package net.emilla.run;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.text.format.DateFormat;

import net.emilla.activity.AssistActivity;

public final class TimePickerOffering implements CommandRun {

    private final OnTimeSetListener mTimeSet;

    public TimePickerOffering(OnTimeSetListener timeSet) {
        mTimeSet = timeSet;
    }

    @Override
    public void run(AssistActivity act) {
        var dialog = new TimePickerDialog(act, 0, mTimeSet, 12, 0, DateFormat.is24HourFormat(act));
        // TODO: this isn't respecting the LineageOS system 24-hour setting.
        // todo: should there be an option for default time to be noon vs. the current time? noon
        //  seems much more reasonable in all cases tbh. infinitely more predictable—who the heck
        //  wants to set a timer for right now?!
        dialog.setOnCancelListener(dlg -> {
            act.onCloseDialog(); // Todo: don't require this.
            act.resume();
        });
        act.prepareForDialog();
        dialog.show();
    }
}
