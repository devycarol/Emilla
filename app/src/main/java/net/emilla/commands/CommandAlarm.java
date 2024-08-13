package net.emilla.commands;

import static android.provider.AlarmClock.*;

import android.app.TimePickerDialog;
import android.content.Intent;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.parsing.TimeParsing;
import net.emilla.utils.Apps;
import net.emilla.utils.Dialogs;

import java.util.ArrayList;

public class CommandAlarm extends CoreDataCommand {
private final Intent mViewIntent = Apps.newTask(ACTION_SHOW_ALARMS);
private final Intent mSetIntent = Apps.newTask(ACTION_SET_ALARM);
private final TimePickerDialog mTimePicker;

@Override @ArrayRes
public int detailsId() {
    return R.array.details_alarm;
}

@Override @StringRes
public int dataHint() {
    return R.string.data_hint_alarm;
}

@Override @DrawableRes
public int icon() {
    return R.drawable.ic_alarm;
}

public CommandAlarm(final AssistActivity act, final String instruct) {
    super(act, instruct, R.string.command_alarm, R.string.instruction_alarm);

    mTimePicker = Dialogs.timePicker(act, R.string.dialog_alarm, (v, hourOfDay, minute) -> {
        putTime(hourOfDay, minute);
        execute(mSetIntent);
    });
}

private void putTime(final int hourOfDay, final int minute) {
    mSetIntent.putExtra(EXTRA_HOUR, hourOfDay);
    mSetIntent.putExtra(EXTRA_MINUTES, minute);
}

private void putTime(final String time) {
    final int[] timeUnits = TimeParsing.parseTime(time);
    final int[] weekdays = TimeParsing.parseWeekdays(time);
    switch (timeUnits[3]) { // TODO: change how this is handled - toasts conflict with the AOSP clock
    case 1 -> toast("Warning! Alarm has been set for AM by default.", true);
    case 2 -> toast("Warning! Alarm has been set for PM by default.", true);
    }
    putTime(timeUnits[0], timeUnits[1]);
    ArrayList<Integer> weekdayList = new ArrayList<>();
    int i;
    for (i = 0; i < weekdays.length; ++i) weekdayList.add(weekdays[i]);
    if (i > 0) mSetIntent.putExtra(EXTRA_DAYS, weekdayList);
}

private void putLabel(final String label) {
    mSetIntent.putExtra(EXTRA_MESSAGE, label);
}

private void execute(final Intent intent) {
    if (intent.resolveActivity(packageManager()) == null) throw new EmlaAppsException("No alarm clock app found on your device."); // TODO: handle at mapping - both intents will probably need to be checked
    succeed(intent);
}

@Override
protected void run() {
    execute(mViewIntent); // todo: put time picker on this one, separate one-word (new interface) command for "alarms"
//    mTimePicker.setOnCancelListener(dialog -> mActivity.onCloseDialog());
}

@Override
protected void run(final String time) {
    putTime(time);
    execute(mSetIntent);
}

@Override
protected void runWithData(final String label) {
    putLabel(label);
    mTimePicker.setOnCancelListener(dialog -> {
        mSetIntent.removeExtra(EXTRA_MESSAGE);
        onCloseDialog(true); // todo: this one isn't pulling the keyboard back up for some reason
    });
    offer(mTimePicker);
}

@Override
protected void runWithData(final String time, final String label) {
    putTime(time);
    putLabel(label);
    run();
}
}
