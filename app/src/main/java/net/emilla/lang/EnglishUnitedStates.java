package net.emilla.lang;

import android.content.Context;
import android.text.format.DateFormat;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.exception.EmillaException;
import net.emilla.grammar.TextStream;
import net.emilla.lang.date.HourMinute;
import net.emilla.lang.date.Meridiem;
import net.emilla.lang.date.Time;
import net.emilla.random.Dice;
import net.emilla.random.DiceRoller;
import net.emilla.util.Patterns;
import net.emilla.util.Strings;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.regex.Pattern;
import java.util.stream.Stream;

enum EnglishUnitedStates {;
    private static final Pattern HOUR_MINUTE = Pattern.compile(
        "([01]?[0-9]|2[0-3])(:?[0-5][0-9])? *([AP]M?)?",
        Pattern.CASE_INSENSITIVE
    );
    private static final Pattern WEEKDAYS = Pattern.compile("\\w+( +\\w+){0,6}");
    private static final Pattern WEEKDAY_LETTERS = Pattern.compile("[umtwrfs]{1,7}");
    private static final Pattern COMMA_OPTIONAL_LIST = Pattern.compile(", *| +");

    @Nullable
    public static DiceRoller diceRoller(String roll) {
        var stream = new TextStream(roll);
        Dice dice = diceFrom(stream, false);
        if (dice == null) {
            return null;
        }

        var roller = new DiceRoller();
        roller.add(dice);

        while (stream.hasRemaining()) {
            boolean minus = stream.skip('-');
            if (minus || stream.skip('+')) {
                dice = diceFrom(stream, minus);
                if (dice == null) {
                    return null;
                }
                roller.add(dice);
            } else {
                return null;
            }
        }

        return roller;
    }

    @Nullable
    private static Dice diceFrom(TextStream stream, boolean minus) {
        TextStream.Bookmark start = stream.position();

        Integer rollCount = stream.integer();
        if (rollCount == null) {
            return null;
        }

        if (!stream.skip('d')) {
            return Dice.modifier(rollCount);
        }

        Integer faceCount = stream.integer();
        if (faceCount == null || faceCount <= 0) {
            stream.reset(start);
            return null;
        }

        return new Dice(
            minus
                ? -rollCount
                : rollCount
            ,
            faceCount
        );
    }

    public static HourMinute hourMinute(String time, Context ctx, @StringRes int errorTitle) {
        var meridiem
            = Time.REGEX_AM.matcher(time).find() ? Meridiem.AM
            : Time.REGEX_PM.matcher(time).find() ? Meridiem.PM
            : Meridiem.UNSPECIFIED
        ;
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

        return new HourMinute(hour, minute);
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

    @Nullable
    public static EnumSet<DayOfWeek> weekdays(String time, @StringRes int errorTitle) {
        String weekdayString = EnglishUnitedStates.HOUR_MINUTE.matcher(time.toLowerCase()).replaceFirst("").trim();
        if (weekdayString.isEmpty()) {
            return null;
        }

        var weekdays = EnumSet.noneOf(DayOfWeek.class);
        weekdayStream(weekdayString, errorTitle).forEach(
            dayOfWeek -> {
                if (!weekdays.add(dayOfWeek)) {
                    throw new EmillaException(errorTitle, R.string.error_excess_weekdays);
                }
            }
        );

        return weekdays;
    }

    private static Stream<DayOfWeek> weekdayStream(CharSequence weekdays, @StringRes int errorTitle) {
        if (WEEKDAY_LETTERS.matcher(weekdays).matches()) {
            return weekdays.chars()
                .mapToObj((int letter) -> weekdayOf(letter, errorTitle));
        }

        if (WEEKDAYS.matcher(weekdays).matches()) {
            return Patterns.splitStream(COMMA_OPTIONAL_LIST, weekdays)
                .map((String word) -> weekdayOf(word, errorTitle));
        }

        throw formatFail(errorTitle);
    }

    private static DayOfWeek weekdayOf(int letter, @StringRes int errorTitle) {
        return switch (letter) {
            case 'u' -> DayOfWeek.SUNDAY;
            case 'm' -> DayOfWeek.MONDAY;
            case 't' -> DayOfWeek.TUESDAY;
            case 'w' -> DayOfWeek.WEDNESDAY;
            case 'r' -> DayOfWeek.THURSDAY;
            case 'f' -> DayOfWeek.FRIDAY;
            case 's' -> DayOfWeek.SATURDAY;
            default -> throw formatFail(errorTitle);
        };
    }

    private static DayOfWeek weekdayOf(String word, @StringRes int errorTitle) {
        return switch (word) {
            case "sunday", "sun", "u" -> DayOfWeek.SUNDAY;
            case "monday", "mon", "m" -> DayOfWeek.MONDAY;
            case "tuesday", "tues", "tue", "t" -> DayOfWeek.TUESDAY;
            case "wednesday", "wed", "w" -> DayOfWeek.WEDNESDAY;
            case "thursday", "thurs", "thur", "thu", "th", "r" -> DayOfWeek.THURSDAY;
            case "friday", "fri", "f" -> DayOfWeek.FRIDAY;
            case "saturday", "sat", "s" -> DayOfWeek.SATURDAY;
            default -> throw formatFail(errorTitle);
        };
    }

    private static EmillaException formatFail(@StringRes int errorTitle) {
        return new EmillaException(errorTitle, R.string.error_invalid_weekday_format);
    }
}
