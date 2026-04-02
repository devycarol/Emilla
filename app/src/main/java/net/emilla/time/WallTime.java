package net.emilla.time;

import android.content.Context;
import android.text.format.DateFormat;

import androidx.annotation.Nullable;

import java.time.LocalTime;

public final class WallTime {
    private static final int MINUTE_OF_NOON = 12 * 60;
    private static final int MINUTES_IN_DAY = MINUTE_OF_NOON * 2;
    private static final int SECOND_OF_NOON = MINUTE_OF_NOON * 60;
    private static final int SECONDS_IN_DAY = SECOND_OF_NOON * 2;

    private final int mMinuteOfDay;
    private final boolean mMeridiemIsKnown;

    private WallTime(int hour, int minute, boolean meridiemIsKnown) {
        mMinuteOfDay = hour * 60 + minute;
        mMeridiemIsKnown = meridiemIsKnown;
    }

    @Nullable
    public static WallTime of(Context ctx, int hour, int minute, @Nullable Meridiem meridiem) {
        if (minute < 0 || 59 < minute) {
            return null;
        }

        if (meridiem == null) {
            if (hour < 0 || 23 < hour) {
                return null;
            }

            return new WallTime(
                hour,
                minute,
                hour == 0 || hour >= 13 || DateFormat.is24HourFormat(ctx)
                // todo: this doesn't respect LineageOS 24h time
            );
        }

        if (hour < 1 || 12 < hour) {
            return null;
        }

        if (meridiem == Meridiem.PM) {
            if (hour != 12) {
                hour += 12;
            }
        } else {
            if (hour == 12) {
                hour = 0;
            }
        }

        return new WallTime(hour, minute, true);
    }

    private static int until(int start, int end, int period) {
        return Math.floorMod(end - start, period);
    }

    public HourMinute nextOccurrence() {
        int minuteOfNext = mMinuteOfDay;
        if (!mMeridiemIsKnown) {
            var now = LocalTime.now();
            int minuteOfNow = now.getHour() * 60 + now.getMinute();
            int minutesUntil = until(minuteOfNow, minuteOfNext, MINUTES_IN_DAY);
            if (minutesUntil == 0 || minutesUntil > MINUTE_OF_NOON) {
                minuteOfNext = until(MINUTE_OF_NOON, minuteOfNext, MINUTES_IN_DAY);
            }
        }

        return new HourMinute(minuteOfNext / 60, minuteOfNext % 60);
    }

    public int secondsToNextOccurrence() {
        var now = LocalTime.now();
        int secondOfNow = now.toSecondOfDay();
        if (now.getNano() != 0) {
            // ceiling the current second to give some leeway for alarms to fire
            ++secondOfNow;
        }

        int period = mMeridiemIsKnown
            ? SECONDS_IN_DAY
            : SECOND_OF_NOON
        ;
        return until(secondOfNow, mMinuteOfDay * 60, period);
    }
}
