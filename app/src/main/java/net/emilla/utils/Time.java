package net.emilla.utils;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.max;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.util.Calendar.*;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;

import java.util.Calendar;
import java.util.regex.Matcher;

public final class Time { // TODO LAAAAAAAAAAAAAAAAAAAAAAAAANG TODO LANG

    private static final String JAN = "jan(uary)?", FEB = "feb(ruary)?", MAR = "mar(ch)?";
    private static final String APR = "apr(il)?", MAY = "may", JUN = "june?";
    private static final String JUL = "july?", AUG = "aug(ust)?", SEP = "sep(t(ember)?)?";
    private static final String OCT = "oct(ober)?", NOV = "nov(ember)?", DEC = "dec(ember)?";
    private static final String MONTH_RGX = format("(%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s)",
            JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC);
    private static final String DAY_RGX = "(^| +)\\d?\\d( +|$)";
    private static final String YEAR_RGX = "(\\d\\d|')\\d\\d";

    private static final String SUN = "(u|sun(day)?)", MON = "m(on(day)?)?";
    private static final String TUE = "t(ue(s(day)?)?)?", WED = "w(ed(nesday)?)?";
    private static final String THU = "(h|th(u(r(s(day)?)?)?)?)";
    private static final String FRI = "f(ri(day)?)?", SAT = "s(at(urday)?)?";
    private static final String WEEKDAY_RGX = format("(%s|%s|%s|%s|%s|%s|%s)",
            SUN, MON, TUE, WED, THU, FRI, SAT);

    private static boolean containsRgxIgnoreCase(String s, String rgx) {
        return compile(rgx, CASE_INSENSITIVE).matcher(s).find();
    }

    private static int[] splitTimeString(String time) {
        time = time.replaceAll("\\D", "");
        if (!time.matches("\\d{1,6}")) throw new EmlaBadCommandException(R.string.error, R.string.error_invalid_time);

        int h = 0, m = 0, s = 0;
        int len = time.length();
        switch ((len + 1) / 2) {
        case 1 -> h = parseInt(time);
        case 2 -> {
            h = parseInt(time.substring(0, len - 2));
            m = parseInt(time.substring(len - 2));
        }
        case 3 -> {
            h = parseInt(time.substring(0, len - 4));
            m = parseInt(time.substring(len - 4, len - 2));
            s = parseInt(time.substring(len - 2));
        }}

        return new int[]{h, m, s, 0};
    }

    public static int[] parseTime(String time) {
        int period;
        if (time.matches("(?i).*\\d *A.*")) period = AM;
        else if (time.matches("(?i).*\\d *P.*")) period = PM;
        else period = -1;

        int[] units = splitTimeString(time);
        int h = units[0], m = units[1], s = units[2], warn = 0;

        if (period == -1) {
            if (1 <= h && h <= 11) warn = 1;
            if (h == 12) warn = 2;
        } else {
            if (1 > h || h > 12) throw new EmlaBadCommandException(R.string.error, R.string.error_invalid_time);
            if (h == 12) h = 0;
            if (period == PM) h += 12;
        }

        if (h > 23 || max(m, s) > 59) throw new EmlaBadCommandException(R.string.error, R.string.error_invalid_time);

        return new int[]{h, m, s, warn};
    }

    private static int[] parseDurationUntil(String until) {
        int[] endUnits = parseTime(until);
        int endHour = endUnits[0], endMin = endUnits[1], endSec = endUnits[2];

        Calendar cal = getInstance();
        int curHour = cal.get(HOUR_OF_DAY), curMin = cal.get(MINUTE), curSec = cal.get(SECOND);

        int h = 0, m = 0, s = 0, warn = 0;

        s = endSec - curSec;
        if (s < 0) {
            s += 60;
            --m;
        }
        m += endMin - curMin;
        if (m < 0) {
            m += 60;
            --h;
        }
        h += endHour - curHour;
        if (h < 0) h += 24;

        if (!containsRgxIgnoreCase(until, "\\d *[AP]")) {
            if (h < 12) warn = 1;
            else warn = 2;

            if (h > 12 || h == 12 && max(m, s) > 0) h -= 12;
        }

        return new int[]{h, m, s, warn, endHour, endMin};
    }

    private static int[] splitDurationString (String dur) {
        String[] units = dur.split(" *: *| +");

        double h = 0.0, m = 0.0, s = 0.0;
        switch (units.length) {
        case 3:
            s = parseDouble(units[2]);
            // fallthrough
        case 2:
            h = parseDouble(units[0]);
            m = parseDouble(units[1]);
            break;
        case 1:
            m = parseDouble(dur);
        }

        m += h % 1.0 * 60.0;
        s += m % 1.0 * 60.0;

        return new int[]{(int) h, (int) m, (int) s, 0};
    }

    public static int[] parseDuration(String dur) { // TODO: handle 24h time properly
        if (dur.matches("(until|t(ill?|o)) .*")) return parseDurationUntil(dur);
        else {
            double[] timeUnits = new double[4];
            String[] patterns = {
                    "\\d*\\.?\\d+ *h((ou)?rs?)?",
                    "\\d*\\.?\\d+ *m(in(ute)?s?)?",
                    "\\d*\\.?\\d+ *s(ec(ond)?s?)?"
            };

            boolean hit = false, notEmpty = true;
            for (int i = 0; i < 3 && notEmpty; ++i) {
                String rgx = patterns[i];
                if (containsRgxIgnoreCase(dur, rgx)) {
                    hit = true;

                    String[] sansHrs = dur.split(rgx);
                    if (sansHrs.length > 2) throw new EmlaBadCommandException(R.string.command_timer, R.string.error_invalid_duration);

                    String before = "", after = "";
                    switch (sansHrs.length) {
                    case 2: after = sansHrs[1];
                    // fallthrough
                    case 1: before = sansHrs[0];
                    }

                    int start = before.length(), end = dur.length() - after.length();

                    timeUnits[i] = parseDouble(dur.substring(start, end).replaceAll("[^\\d.]", ""));
                    dur = (before + after).trim();

                    notEmpty = !dur.isEmpty();
                }
            }

            if (hit) {
                if (notEmpty) throw new EmlaBadCommandException(R.string.command_timer, R.string.error_excess_time_units);

                timeUnits[1] += timeUnits[0] % 1.0 * 60.0;
                timeUnits[2] += timeUnits[1] % 1.0 * 60.0;

                return new int[]{(int) timeUnits[0], (int) timeUnits[1], (int) timeUnits[2], 0};
            }

            return splitDurationString(dur);
        }
    }

    private static int parseMonth(String month) {
        month = month.substring(0, 3).toLowerCase();

        return switch (month) {
            case "jan" -> Calendar.JANUARY;
            case "feb" -> Calendar.FEBRUARY;
            case "mar" -> Calendar.MARCH;
            case "apr" -> Calendar.APRIL;
            case "may" -> Calendar.MAY;
            case "jun" -> Calendar.JUNE;
            case "jul" -> Calendar.JULY;
            case "aug" -> Calendar.AUGUST;
            case "sep" -> Calendar.SEPTEMBER;
            case "oct" -> Calendar.OCTOBER;
            case "nov" -> Calendar.NOVEMBER;
            case "dec" -> Calendar.DECEMBER;
            default -> -1;
        };
    }

    private static Calendar parseDate(String s) { // TODO more formats but dear god locales ughhhhh
        int day, month, year;
        Calendar cal = getInstance();
        cal.set(MILLISECOND, 0);
        cal.set(SECOND, 0);
        cal.add(MINUTE, -cal.get(MINUTE) % 30 + 30);

        if (s.equalsIgnoreCase("tomorrow")) {
            cal.add(DAY_OF_MONTH, 1);
            return cal;
        }

        Matcher m = compile("(?i)" + MONTH_RGX).matcher(s);
        if (m.find()) {
            cal.set(MONTH, parseMonth(m.group()));
            if (m.find()) throw new EmlaBadCommandException(R.string.command_calendar, R.string.error_excess_time_units);
        }

        m = compile(DAY_RGX).matcher(s);
        if (m.find()) {
            cal.set(DAY_OF_MONTH, parseInt(m.group().trim()));
            if (m.find()) throw new EmlaBadCommandException(R.string.command_calendar, R.string.error_excess_time_units);
        }

        m = compile(YEAR_RGX).matcher(s);
        if (m.find()) {
            String y = m.group();
            if (m.find()) throw new EmlaBadCommandException(R.string.command_calendar, R.string.error_excess_time_units);

            int tickIdx = y.indexOf('\'');
            if (tickIdx == -1) cal.set(YEAR, parseInt(y));
            else cal.set(YEAR, cal.get(YEAR) / 100 * 100 + parseInt(y.substring(tickIdx + 1)));
        }

        return cal;
    }

    public static long[] parseDateAndTimes(String date) {
        String[] dateTime = date.split(" *@ *|(^| +)(at|from) +");

        int[] startTime = null, endTime = null;
        boolean endTomorrow = false; // TODO
        int len = dateTime.length;
        if (len == 2) {
            date = dateTime[0];
            String[] times = dateTime[1].split(" *(-|t(o|ill?)|until) *");

            switch (times.length) {
            case 2:
                String endStr = times[1];
                if (containsRgxIgnoreCase(endStr, "tomorrow")) {
                    endTomorrow = true;
                    endStr = endStr.replaceFirst(" *tomorrow", "");
                } else if (containsRgxIgnoreCase(endStr, "on "))
                endTime = parseTime(endStr);
                // fallthrough
            case 1:
                startTime = parseTime(times[0]);
                break;
            default: throw new EmlaBadCommandException(R.string.command_calendar, R.string.error_excess_timespan);
            }
        } else if (len != 1) throw new EmlaBadCommandException(R.string.command_calendar, R.string.error_excess_timespans);

        Calendar cal = parseDate(date);
        Calendar endCal = null;
        if (startTime != null) {
            if (endTime != null) {
                endCal = (Calendar) cal.clone();

                endCal.set(HOUR_OF_DAY, endTime[0]);
                endCal.set(MINUTE, endTime[1]);
                endCal.set(SECOND, endTime[2]);
            }

            cal.set(HOUR_OF_DAY, startTime[0]);
            cal.set(MINUTE, startTime[1]);
            cal.set(SECOND, startTime[2]);
        }

        return new long[]{cal.getTimeInMillis(), endCal == null ? 0 : endCal.getTimeInMillis()};
    }

    private static char parseWeekday(String s) {
        return switch (s) {
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

    private static String buildWeekdayString(String s) {
        String[] words = s.split(", *| +");
        StringBuilder sb = new StringBuilder();

        for (String word : words) sb.append(parseWeekday(word));

        return sb.toString();
    }

    public static int[] parseWeekdays(String s) {
        s = s.toLowerCase().replaceAll("\\d?\\d *[ap]m?|[^\\w, ]+|\\d+", "").trim();
        if (s.isEmpty()) return new int[0];
        if (!s.matches("\\w+( +\\w+){0,6}")) throw new EmlaBadCommandException(R.string.command_alarm, R.string.error_invalid_weekday_format);

        if (!s.matches("[umtwrfs]{1,7}")) s = buildWeekdayString(s);

        int[] weekdays = new int[s.length()];
        int i = 0;
        do {
            char c = s.charAt(0);
            s = s.substring(1);

            if (s.indexOf(c) == -1) {
                switch (c) {
                case 'u' -> weekdays[i] = Calendar.SUNDAY;
                case 'm' -> weekdays[i] = Calendar.MONDAY;
                case 't' -> weekdays[i] = Calendar.TUESDAY;
                case 'w' -> weekdays[i] = Calendar.WEDNESDAY;
                case 'r' -> weekdays[i] = Calendar.THURSDAY;
                case 'f' -> weekdays[i] = Calendar.FRIDAY;
                case 's' -> weekdays[i] = Calendar.SATURDAY;
                }
            } else throw new EmlaBadCommandException(R.string.command_alarm, R.string.error_excess_weekdays);

            ++i;
        }
        while (!s.isEmpty());

        return weekdays;
    }

    private Time() {}
}
