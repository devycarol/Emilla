package net.emilla.command.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.AlarmClock;

import androidx.annotation.Nullable;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.annotation.internal;
import net.emilla.lang.Lang;
import net.emilla.time.HourMinute;
import net.emilla.time.WallTime;
import net.emilla.util.Apps;
import net.emilla.wadget.ActionSurface;
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
    protected Feedback run(ActionSurface surface) {
        if (mWeekdays.anyAreSet()) {
            return Feedback.pend();
        }

        return Feedback.succeed(new Intent(AlarmClock.ACTION_SHOW_ALARMS));
    }

    @Override
    protected Feedback run(ActionSurface surface, String time) {
        return runWithData(surface, time, null);
    }

    @Override
    public Feedback runWithData(ActionSurface surface, String label) {
        return Feedback.pend();
    }

    @Override
    public Feedback runWithData(ActionSurface surface, String time, @Nullable String label) {
        WallTime wallTime = Lang.wallTime(surface.getContext(), time);
        if (wallTime == null) {
            return failMessage(R.string.error_invalid_time);
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

        return Feedback.succeed(setAlarm);
    }
}
