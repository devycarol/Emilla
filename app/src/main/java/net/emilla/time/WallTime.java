package net.emilla.time;

import android.content.Context;
import android.text.format.DateFormat;

import androidx.annotation.Nullable;

import java.time.LocalTime;

public final class WallTime {
    // TODO: simplify this class by just storing a minute count and using modular subtraction to do
    //  meridiem flips and such.
    private final int mHour;
    private final int mMinute;
    private final boolean mMeridiemIsKnown;

    private WallTime(int hour, int minute, boolean meridiemIsKnown) {
        mHour = hour;
        mMinute = minute;
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

    public HourMinute nextOccurrence() {
        return new HourMinute(
            shouldFlipMeridiem()
                ? mHour == 12
                    ? 0
                    : mHour + 12
                : mHour
            ,
            mMinute
        );
    }

    private boolean shouldFlipMeridiem() {
        if (mMeridiemIsKnown) {
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

    public int secondsToNextOccurrence() {
        var now = LocalTime.now();

        int minute = 0;
        int second = -now.getSecond();
        if (second < 0) {
            second += 60;
            --minute;
        }

        int hour = 0;
        minute += mMinute - now.getMinute();
        if (minute < 0) {
            minute += 60;
            --hour;
        }

        hour += mHour - now.getHour();
        if (hour < 0) {
            hour += 24;
        }

        if (!mMeridiemIsKnown) {
            if (hour > 12 || hour == 12 && (minute > 0 || second > 0)) {
                hour -= 12;
            }
        }

        return hour * 60 * 60 + minute * 60 + second - 1;
        // we subtract 1 to give an extra second of leeway
    }
}
