package net.emilla.command.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.AlarmClock;

import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.lang.Lang;
import net.emilla.time.HourMinute;
import net.emilla.time.WallTime;
import net.emilla.util.Apps;
import net.emilla.widget.WeekdayWidget;

import java.util.ArrayList;

final class Alarm extends CoreDataCommand {
    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, new Intent(AlarmClock.ACTION_SHOW_ALARMS))
            || Apps.canDo(pm, new Intent(AlarmClock.ACTION_SET_ALARM))
        ;
    }

    private final WeekdayWidget mWeekdays = WeekdayWidget.COOKED;

    @internal Alarm(Context ctx) {
        super(ctx, CoreEntry.ALARM, R.string.data_hint_label);

        giveGadgets(mWeekdays);
    }

    @Override
    protected void run(AssistActivity act) {
        if (mWeekdays.anyAreSet()) {
            act.offer(a -> {});
            return;
        }

        Apps.succeed(act, new Intent(AlarmClock.ACTION_SHOW_ALARMS));
    }

    @Override
    protected void run(AssistActivity act, String time) {
        runWithData(act, time, null);
    }

    @Override
    public void runWithData(AssistActivity act, String label) {
        act.offer(a -> {});
    }

    @Override
    public void runWithData(AssistActivity act, String time, @Nullable String label) {
        WallTime wallTime = Lang.wallTime(act, time);
        if (wallTime == null) {
            failMessage(act, R.string.error_invalid_time);
            return;
        }

        HourMinute hourMinute = wallTime.nextOccurrence();
        Intent setAlarm = hourMinute.setAlarm();
        if (label != null) {
            setAlarm.putExtra(AlarmClock.EXTRA_MESSAGE, label);
        }

        ArrayList<Integer> weekdays = mWeekdays.calendarArrayList();
        if (weekdays != null) {
            setAlarm.putExtra(AlarmClock.EXTRA_DAYS, weekdays);
        }

        Apps.succeed(act, setAlarm);
    }
}
