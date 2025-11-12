package net.emilla.lang.date.impl;

import android.content.Context;
import android.text.format.DateFormat;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.exception.EmillaException;
import net.emilla.lang.date.HourMin;
import net.emilla.lang.date.Meridiem;
import net.emilla.lang.date.Time;
import net.emilla.util.Strings;

import java.time.LocalTime;
import java.util.regex.Pattern;

public record HourMinEN_US(int hour24, int minute) implements HourMin {

    public static final Pattern REGEX = Pattern.compile(
        "([01]?[0-9]|2[0-3])(:?[0-5][0-9])? *([AP]M?)?",
        Pattern.CASE_INSENSITIVE
    );

    public static HourMin instance(String time, Context ctx, @StringRes int errorTitle) {
        var meridiem
            = Time.REGEX_AM.matcher(time).find() ? Meridiem.AM
            : Time.REGEX_PM.matcher(time).find() ? Meridiem.PM
            : Meridiem.UNSPECIFIED;

        String timeDigits = Strings.stripNonDigits(time);
        if (!Strings.isOneToNDigits(timeDigits, 4)) {
            throw new EmillaException(errorTitle, R.string.error_invalid_time);
        }

        int hour;
        int minute;
        int len = timeDigits.length();
        if (len > 2) {
            hour = Integer.parseInt(timeDigits.substring(0, len - 2));
            minute = Integer.parseInt(timeDigits.substring(len - 2));
        } else {
            hour = Integer.parseInt(timeDigits);
            minute = 0;
        }

        if (meridiem == Meridiem.UNSPECIFIED) {
            if (shouldFlipMeridiem(ctx, hour, minute)) {
                hour += 12;
                if (hour == 24) {
                    hour = 0;
                }
            }
        } else {
            if (hour < 1 || 12 < hour) {
                throw new EmillaException(errorTitle, R.string.error_invalid_time);
            }
            // hours outside of [1, 12] are invalid.
            if (hour == 12) hour = 0;
            // 12 AM is 0, 12 PM is 12.
            if (meridiem == Meridiem.PM) hour += 12;
            // advance hour by 12 to convert to PM.
        }

        if (hour > 23 || minute > 59) {
            throw new EmillaException(errorTitle, R.string.error_invalid_time);
        }

        return new HourMinEN_US(hour, minute);
    }

    private static boolean shouldFlipMeridiem(Context ctx, int hour, int minute) {
        if (hour < 1 || 12 < hour || DateFormat.is24HourFormat(ctx)) {
            // todo: this doesn't respect LineageOS 24h time
            return false;
        }

        var timeNow = LocalTime.now();
        int hour24Now = timeNow.getHour();
        if (hour24Now == 0) {
            hour24Now = 24;
            // handle 12am as "24pm"
        }

        int minuteNow = timeNow.getMinute();
        if (hour24Now <= 12) {
            return hour < hour24Now
                || hour == hour24Now && minute <= minuteNow;
            // beginning 1am until 1pm, we need to flip the meridiem of the given time if it's
            // earlier or equal to the current time.
        } else {
            hour24Now -= 12;
            return hour > hour24Now
                || hour == hour24Now && minute > minuteNow;
            // beginning 1pm until 1am, we need to flip the meridiem of the given time if it's later
            // than the time 12 hours ago.
        }
    }
}
