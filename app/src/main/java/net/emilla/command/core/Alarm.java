package net.emilla.command.core;

import static android.provider.AlarmClock.ACTION_SET_ALARM;
import static android.provider.AlarmClock.ACTION_SHOW_ALARMS;
import static android.provider.AlarmClock.EXTRA_DAYS;
import static android.provider.AlarmClock.EXTRA_HOUR;
import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static android.provider.AlarmClock.EXTRA_MINUTES;

import android.content.Intent;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.lang.Lang;
import net.emilla.lang.date.HourMin;
import net.emilla.lang.date.Weekdays;
import net.emilla.settings.Aliases;

public final class Alarm extends CoreDataCommand {

    public static final String ENTRY = "alarm";
    @StringRes
    public static final int NAME = R.string.command_alarm;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_alarm;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Alarm::new, ENTRY, NAME, ALIASES);
    }

    public Alarm(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_alarm,
              R.drawable.ic_alarm,
              R.string.summary_alarm,
              R.string.manual_alarm,
              R.string.data_hint_label);
    }

    private Intent makeIntent() {
        return new Intent(ACTION_SHOW_ALARMS);
    }

    private Intent makeIntent(String timeString) {
        HourMin time = Lang.time(timeString, activity, NAME);
        Weekdays days = Lang.weekdays(timeString, NAME);
        Intent in = makeIntent(time.hour24(), time.minute());
        return days.empty() ? in : in.putExtra(EXTRA_DAYS, days.days());
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
