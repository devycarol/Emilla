package net.emilla.time;

import android.content.Intent;
import android.provider.AlarmClock;

import net.emilla.annotation.internal;

public final class HourMinute {
    private final int mHour24;
    private final int mMinute;

    @internal HourMinute(int hour24, int minute) {
        mHour24 = hour24;
        mMinute = minute;
    }

    public Intent setAlarm() {
        return new Intent(AlarmClock.ACTION_SET_ALARM)
            .putExtra(AlarmClock.EXTRA_HOUR, mHour24)
            .putExtra(AlarmClock.EXTRA_MINUTES, mMinute)
        ;
    }
}
