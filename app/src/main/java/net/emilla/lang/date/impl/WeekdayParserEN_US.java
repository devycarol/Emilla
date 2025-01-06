package net.emilla.lang.date.impl;

import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;
import net.emilla.lang.date.WeekdayParser;

import java.util.ArrayList;
import java.util.Calendar;

public record WeekdayParserEN_US(ArrayList<Integer> days, boolean noDays) implements WeekdayParser {

    public static WeekdayParserEN_US instance(String timeStr) {
        ArrayList<Integer> days = parseWeekdays(timeStr);
        return new WeekdayParserEN_US(days, days.isEmpty());
    }

    private static ArrayList<Integer> parseWeekdays(String s) {
        s = s.toLowerCase().replaceAll("\\d?\\d *[ap]m?|[^\\w, ]+|\\d+", "").trim();
        if (s.isEmpty()) return new ArrayList<>();
        if (!s.matches("\\w+( +\\w+){0,6}")) throw new EmlaBadCommandException(R.string.command_alarm, R.string.error_invalid_weekday_format);

        if (!s.matches("[umtwrfs]{1,7}")) s = letterString(s);

        ArrayList<Integer> weekdays = new ArrayList<>(s.length());
        do {
            char c = s.charAt(0);
            s = s.substring(1);

            if (s.indexOf(c) == -1) {
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
        } while (!s.isEmpty());

        return weekdays;
    }

    /**
     * @return the weekday set in "umtwrfs" format.
     */
    private static String letterString(String s) {
        String[] words = s.split(", *| +");
        StringBuilder sb = new StringBuilder();
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
