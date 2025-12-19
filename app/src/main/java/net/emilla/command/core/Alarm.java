package net.emilla.command.core;

import static android.provider.AlarmClock.ACTION_SET_ALARM;
import static android.provider.AlarmClock.ACTION_SHOW_ALARMS;
import static android.provider.AlarmClock.EXTRA_DAYS;
import static android.provider.AlarmClock.EXTRA_HOUR;
import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static android.provider.AlarmClock.EXTRA_MINUTES;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.lang.Lang;
import net.emilla.lang.date.HourMin;
import net.emilla.lang.date.Weekdays;
import net.emilla.util.Apps;

import java.time.DayOfWeek;
import java.util.EnumSet;

final class Alarm extends CoreDataCommand {

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, makeIntent()) || Apps.canDo(pm, new Intent(ACTION_SET_ALARM));
    }

    @internal Alarm(Context ctx) {
        super(ctx, CoreEntry.ALARM, R.string.data_hint_label);
    }

    @Override
    protected void run(AssistActivity act) {
        appSucceed(act, makeIntent());
        // todo: put time picker on this one. separate "alarms" command will require implementing
        //  the special no-args cmd tree behavior :P
    }

    @Override
    protected void run(AssistActivity act, String time) {
        appSucceed(act, makeIntent(act, time));
    }

    @Override
    public void runWithData(AssistActivity act, String label) {
        offerTimePicker(
            act,
            (picker, hourOfDay, minute) -> {
                appSucceed(act, makeIntent(hourOfDay, minute).putExtra(EXTRA_MESSAGE, label));
            }
        );
        // todo: weekday widget
    }

    @Override
    public void runWithData(AssistActivity act, String time, String label) {
        appSucceed(act, makeIntent(act, time).putExtra(EXTRA_MESSAGE, label));
    }

    private static Intent makeIntent() {
        return new Intent(ACTION_SHOW_ALARMS);
    }

    private static Intent makeIntent(Context ctx, String timeString) {
        HourMin time = Lang.time(timeString, ctx, CoreEntry.ALARM.name);
        Intent setAlarm = makeIntent(time.hour24(), time.minute());

        EnumSet<DayOfWeek> weekdays = Lang.weekdays(timeString, CoreEntry.ALARM.name);
        if (weekdays != null) {
            setAlarm.putExtra(EXTRA_DAYS, Weekdays.calendarArrayList(weekdays));
        }

        return setAlarm;
    }

    private static Intent makeIntent(int hour, int minute) {
        return new Intent(ACTION_SET_ALARM)
            .putExtra(EXTRA_HOUR, hour)
            .putExtra(EXTRA_MINUTES, minute);
    }

}
