package net.emilla.lang.date.impl;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.exception.EmillaException;
import net.emilla.util.Patterns;

import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class WeekdaysEN_US {

    private static final Pattern WEEKDAYS = Pattern.compile("\\w+( +\\w+){0,6}");
    private static final Pattern WEEKDAY_LETTERS = Pattern.compile("[umtwrfs]{1,7}");
    private static final Pattern COMMA_OPTIONAL_LIST = Pattern.compile(", *| +");

    @Nullable
    public static EnumSet<DayOfWeek> set(String time, @StringRes int errorTitle) {
        String weekdayString = HourMinEN_US.REGEX.matcher(time.toLowerCase()).replaceFirst("").trim();
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

    private WeekdaysEN_US() {}

}
