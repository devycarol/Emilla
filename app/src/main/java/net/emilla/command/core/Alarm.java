package net.emilla.command.core;

import static android.provider.AlarmClock.*;

import android.content.Intent;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.lang.Lang;
import net.emilla.lang.date.TimeParser;
import net.emilla.lang.date.WeekdayParser;

public class Alarm extends CoreDataCommand {

    public static final String ENTRY = "alarm";

    @Override @ArrayRes
    public int details() {
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

    private Intent makeIntent(String timeString) {
        TimeParser time = Lang.timeParser(timeString);
        WeekdayParser days = Lang.weekdayParser(timeString);
        Intent in = makeIntent(time.hour24(), time.minute());
        return days.noDays() ? in : in.putExtra(EXTRA_DAYS, days.days());
    }

    private static Intent makeIntent(int hour, int minute) {
        return new Intent(ACTION_SET_ALARM)
                .putExtra(EXTRA_HOUR, hour)
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
