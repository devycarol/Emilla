package net.emilla.command.core;

import static android.provider.AlarmClock.*;

import android.content.Intent;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Time;

import java.util.ArrayList;

public class Alarm extends CoreDataCommand {

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

    public Alarm(AssistActivity act, String instruct) {
        super(act, instruct, R.string.command_alarm, R.string.instruction_alarm);
    }

    private Intent makeIntent() {
        return new Intent(ACTION_SHOW_ALARMS);
    }

    private Intent makeIntent(String time) {
        int[] units = Time.parseTime(time, activity);
        ArrayList<Integer> weekdays = Time.parseWeekdays(time);
        if (weekdays.isEmpty()) return makeIntent(units[0], units[1]);
        return makeIntent(units[0], units[1]).putExtra(EXTRA_DAYS, weekdays);
    }

    private static Intent makeIntent(int hourOfDay, int minute) {
        return new Intent(ACTION_SET_ALARM)
                .putExtra(EXTRA_HOUR, hourOfDay)
                .putExtra(EXTRA_MINUTES, minute);
    }

    @Override
    protected void run() {
        appSucceed(makeIntent());
        // todo: put time picker on this one. separate "alarms" command will require implementing
        //  the special no-args cmd tree behavior :P
    }

    @Override
    protected void run(String time) {
        appSucceed(makeIntent(time));
    }

    @Override
    protected void runWithData(String label) {
        offerTimePicker((picker, hourOfDay, minute) -> appSucceed(makeIntent(hourOfDay, minute)
                .putExtra(EXTRA_MESSAGE, label)));
        // todo: weekday widget
    }

    @Override
    protected void runWithData(String time, String label) {
        appSucceed(makeIntent(time).putExtra(EXTRA_MESSAGE, label));
    }
}
