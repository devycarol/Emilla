package net.emilla.lang.date.impl;

import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;
import net.emilla.lang.date.Weekdays;

import java.util.ArrayList;
import java.util.Calendar;

public record WeekdaysEN_US(ArrayList<Integer> days, boolean empty) implements Weekdays {

    public static WeekdaysEN_US instance(String timeStr) {
        timeStr = timeStr.toLowerCase().replaceFirst(HourMinEN_US.REGEX, "").trim();
        if (timeStr.isEmpty()) return new WeekdaysEN_US(new ArrayList<>(), true);

        if (!timeStr.matches("\\w+( +\\w+){0,6}")) throw new EmlaBadCommandException(R.string.command_alarm, R.string.error_invalid_weekday_format);
        if (!timeStr.matches("[umtwrfs]{1,7}")) timeStr = letterString(timeStr);

        ArrayList<Integer> weekdays = new ArrayList<>(timeStr.length());
        do {
            char c = timeStr.charAt(0);
            timeStr = timeStr.substring(1);

            if (timeStr.indexOf(c) == -1) {
                switch (c) {
                case 'u' -> weekdays.add(Calendar.SUNDAY);
                case 'm' -> weekdays.add(Calendar.MONDAY);
                case 't' -> weekdays.add(Calendar.TUESDAY);
                case 'w' -> weekdays.add(Calendar.WEDNESDAY);
                case 'r' -> weekdays.add(Calendar.THURSDAY);
                case 'f' -> weekdays.add(Calendar.FRIDAY);
                case 's' -> weekdays.add(Calendar.SATURDAY);
                }
            } else throw new EmlaBadCommandException(R.string.command_alarm, R.string.error_excess_weekdays);
        } while (!timeStr.isEmpty());

        return new WeekdaysEN_US(weekdays, false);
    }

    /**
     * @return the weekday set in "umtwrfs" format.
     */
    private static String letterString(String s) {
        var words = s.split(", *| +");
        var sb = new StringBuilder();
        for (String word : words) sb.append(dayLetter(word));
        return sb.toString();
    }

    private static char dayLetter(String word) {
        return switch (word) {
            case "sunday", "sun", "u" -> 'u';
            case "monday", "mon", "m" -> 'm';
            case "tuesday", "tues", "tue", "t" -> 't';
            case "wednesday", "wed", "w" -> 'w';
            case "thursday", "thurs", "thur", "thu", "th", "r" -> 'r';
            case "friday", "fri", "f" -> 'f';
            case "saturday", "sat", "s" -> 's';
            default -> throw new EmlaBadCommandException(R.string.command_alarm, R.string.error_invalid_weekday);
        };
    }
}
