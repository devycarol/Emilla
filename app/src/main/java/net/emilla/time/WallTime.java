package net.emilla.time;

import android.content.Context;
import android.text.format.DateFormat;

import androidx.annotation.Nullable;

import java.time.LocalTime;

public final class WallTime {
    private final int mHour;
    private final int mMinute;
    private final boolean mMeridiemIsKnown;

    private WallTime(int hour, int minute, boolean meridiemIsKnown) {
        mHour = hour;
        mMinute = minute;
        mMeridiemIsKnown = meridiemIsKnown;
    }

    @Nullable
    public static WallTime of(int hour, int minute, @Nullable Meridiem meridiem) {
        if (minute < 0 || 59 < minute) {
            return null;
        }

        if (meridiem == null) {
            if (hour < 0 || 23 < hour) {
                return null;
            }

            return new WallTime(hour, minute, hour == 0 || hour >= 13);
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

    public HourMinute nextOccurrence(Context ctx) {
        return new HourMinute(
            shouldFlipMeridiem(ctx)
                ? mHour == 12
                    ? 0
                    : mHour + 12
                : mHour
            ,
            mMinute
        );
    }

    private boolean shouldFlipMeridiem(Context ctx) {
        if (mMeridiemIsKnown || DateFormat.is24HourFormat(ctx)) {
            // todo: this doesn't respect LineageOS 24h time
            return false;
        }

        var now = LocalTime.now();
        int hour = now.getHour();
        if (hour == 0) {
            hour = 24;
            // handle 12am as "24pm"
        }

        int minuteNow = now.getMinute();
        if (hour <= 12) {
            return mHour < hour
                || mHour == hour && mMinute <= minuteNow;
            // from 1am to 1pm, we need to flip the meridiem if it's earlier or equal to the current
            // time.
        } else {
            hour -= 12;
            return mHour > hour
                || mHour == hour && mMinute > minuteNow;
            // from 1pm to 1am, we need to flip the meridiem if it's later than the time 12 hours
            // ago.
        }
    }
}
