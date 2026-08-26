package net.emilla.command.core;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.AlarmClock;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.action.Widget;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.datafield.DataDirective;
import net.emilla.datafield.DataField;
import net.emilla.lang.Lang;
import net.emilla.time.HourMinute;
import net.emilla.time.WallTime;
import net.emilla.util.Apps;
import net.emilla.wadget.ActionSurface;
import net.emilla.widget.WeekdayWidget;

import java.util.ArrayList;

final class Alarm extends EmillaCommand {
    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, new Intent(AlarmClock.ACTION_SHOW_ALARMS))
            || Apps.canDo(pm, new Intent(AlarmClock.ACTION_SET_ALARM))
        ;
    }

    private final WeekdayWidget mWeekdays = WeekdayWidget.COOKED;

    @internal Alarm(ActionSurface surface) {
        super(surface, CoreEntry.ALARM);
    }

    @Override
    public DataDirective dataDirective() {
        return new DataField(R.string.data_hint_label);
    }

    @Override
    protected Widget[] widgets() {
        return new Widget[] {
            mWeekdays,
        };
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        if (surface.dataText() != null || mWeekdays.anyAreSet()) {
            return Feedback.pend();
        }

        return Feedback.succeed(new Intent(AlarmClock.ACTION_SHOW_ALARMS));
    }

    @Override
    protected Feedback run(ActionSurface surface, String time) {
        WallTime wallTime = Lang.wallTime(surface.getContext(), time);
        if (wallTime == null) {
            return Feedback.fail(R.string.error_invalid_time);
        }

        HourMinute hourMinute = wallTime.nextOccurrence();
        Intent setAlarm = hourMinute.setAlarm();
        String label = surface.dataText();
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
