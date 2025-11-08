package net.emilla.lang.date;

import static java.util.Calendar.AM;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.PM;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.EmillaActivity;
import net.emilla.exception.EmillaException;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Todo: this class is utterly unreadable
public final class Time { // TODO LAAAAAAAAAAAAAAAAAAAAAAAAANG TODO LANG

    private static final String JAN = "jan(uary)?";
    private static final String FEB = "feb(ruary)?";
    private static final String MAR = "mar(ch)?";
    private static final String APR = "apr(il)?";
    private static final String MAY = "may";
    private static final String JUN = "june?";
    private static final String JUL = "july?";
    private static final String AUG = "aug(ust)?";
    private static final String SEP = "sep(t(ember)?)?";
    private static final String OCT = "oct(ober)?";
    private static final String NOV = "nov(ember)?";
    private static final String DEC = "dec(ember)?";

    private static Pattern monthRegex() {
        return Pattern.compile(
            String.format("(?i)(%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s)",
                          JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC)
        );
    }

    private static Pattern dayRegex() {
        return Pattern.compile("(^| +)\\d?\\d( +|$)");
    }

    private static Pattern yearRegex() {
        return Pattern.compile("(\\d\\d|')\\d\\d");
    }

    private static boolean containsRgxIgnoreCase(String s, String rgx) {
        return Pattern.compile(rgx, CASE_INSENSITIVE).matcher(s).find();
    }

    private static int[] timeUnits(String time, @StringRes int errorTitle) {
        time = time.replaceAll("\\D", "");
        if (!time.matches("\\d{1,6}")) {
            throw new EmillaException(errorTitle, R.string.error_invalid_time);
        }

        int h = 0;
        int m = 0;
        int s = 0;
        int len = time.length();
        switch ((len + 1) / 2) {
        case 1 -> {
            h = Integer.parseInt(time);
        }
        case 2 -> {
            h = Integer.parseInt(time.substring(0, len - 2));
            m = Integer.parseInt(time.substring(len - 2));
        }
        case 3 -> {
            h = Integer.parseInt(time.substring(0, len - 4));
            m = Integer.parseInt(time.substring(len - 4, len - 2));
            s = Integer.parseInt(time.substring(len - 2));
        }}

        return new int[]{h, m, s};
    }

    private static int[] parseTime(
        String time,
        @Nullable EmillaActivity act, /*todo jesus christ*/
        @StringRes int errorTitle
    ) {
        int meridiem;
        if (time.matches("(?i).*\\d *A.*")) meridiem = AM;
        else if (time.matches("(?i).*\\d *P.*")) meridiem = PM;
        else meridiem = -1;

        int[] units = timeUnits(time, errorTitle);
        int h = units[0];
        int m = units[1];
        int s = units[2];

        if (meridiem == -1) {
            if (act != null) {
                if (1 <= h && h <= 11) act.toast("Warning! Time set for AM.");
                else if (h == 12) act.toast("Warning! Time set for PM.");
            }
        } else {
            if (h < 1 || 12 < h) throw new EmillaException(errorTitle, R.string.error_invalid_time);
            if (h == 12) h = 0;
            if (meridiem == PM) h += 12;
        }

        if (h > 23 || Math.max(m, s) > 59) {
            throw new EmillaException(errorTitle, R.string.error_invalid_time);
        }

        return new int[]{h, m, s};
    }

    private static Duration parseDurationUntil(String until, @StringRes int errorTitle) {
        int[] endUnits = parseTime(until, null, errorTitle);
        int endHour = endUnits[0];
        int endMin = endUnits[1];
        int endSec = endUnits[2];

        var cal = Calendar.getInstance();
        int curHour = cal.get(HOUR_OF_DAY);
        int curMin = cal.get(MINUTE);
        int curSec = cal.get(SECOND);

        int h = 0;
        int m = 0;
        int s = 0;

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
        if (h < 0) {
            h += 24;
        }

        if (!containsRgxIgnoreCase(until, "\\d *[AP]")) {
            if (h > 12 || h == 12 && Math.max(m, s) > 0) {
                h -= 12;
            }
        }

        return new Duration(h, m, s - 1);
        // we subtract 1 to give an extra second of leeway
    }

    private static Duration splitDurationString (String dur) {
        String[] units = dur.split(" *: *| +");

        double h = 0.0;
        double m = 0.0;
        double s = 0.0;
        switch (units.length) {
        case 3:
            s = Double.parseDouble(units[2]);
            // fallthrough
        case 2:
            h = Double.parseDouble(units[0]);
            m = Double.parseDouble(units[1]);
            break;
        case 1:
            m = Double.parseDouble(dur);
        }

        m += h % 1.0 * 60.0;
        s += m % 1.0 * 60.0;

        return new Duration((int) h, (int) m, (int) s);
    }

    public static Duration parseDuration(String dur, @StringRes int errorTitle) {
        // TODO: handle 24h time properly
        if (dur.matches("(until|t(ill?|o)) .*")) {
            return parseDurationUntil(dur, errorTitle);
        }

        var timeUnits = new double[3];
        String[] patterns = {
            "\\d*\\.?\\d+ *h((ou)?rs?)?",
            "\\d*\\.?\\d+ *m(in(ute)?s?)?",
            "\\d*\\.?\\d+ *s(ec(ond)?s?)?"
        };

        boolean hit = false;
        boolean notEmpty = true;
        for (int i = 0; i < 3 && notEmpty; ++i) {
            String rgx = patterns[i];
            if (containsRgxIgnoreCase(dur, rgx)) {
                hit = true;

                String[] withoutHours = dur.split(rgx);
                if (withoutHours.length > 2) {
                    throw new EmillaException(errorTitle, R.string.error_invalid_duration);
                }

                String before = "";
                String after = "";
                switch (withoutHours.length) {
                case 2: after = withoutHours[1];
                // fallthrough
                case 1: before = withoutHours[0];
                }

                int start = before.length();
                int end = dur.length() - after.length();

                timeUnits[i] = Double.parseDouble(dur.substring(start, end).replaceAll("[^\\d.]", ""));
                dur = (before + after).trim();

                notEmpty = !dur.isEmpty();
            }
        }

        if (hit) {
            if (notEmpty) {
                throw new EmillaException(errorTitle, R.string.error_excess_time_units);
            }

            timeUnits[1] += timeUnits[0] % 1.0 * 60.0;
            timeUnits[2] += timeUnits[1] % 1.0 * 60.0;

            return new Duration((int) timeUnits[0], (int) timeUnits[1], (int) timeUnits[2]);
        }

        return splitDurationString(dur);
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

    private static Calendar parseDate(String s, @StringRes int errorTitle) {
        // TODO more formats but dear god locales ughhhhh
        var cal = Calendar.getInstance();
        cal.set(MILLISECOND, 0);
        cal.set(SECOND, 0);
        cal.add(MINUTE, -cal.get(MINUTE) % 30 + 30);

        if (s.equalsIgnoreCase("tomorrow")) {
            cal.add(DAY_OF_MONTH, 1);
            return cal;
        }

        Matcher m = monthRegex().matcher(s);
        if (m.find()) {
            cal.set(MONTH, parseMonth(m.group()));
            if (m.find()) throw new EmillaException(errorTitle, R.string.error_excess_time_units);
        }

        m = dayRegex().matcher(s);
        if (m.find()) {
            cal.set(DAY_OF_MONTH, Integer.parseInt(m.group().trim()));
            if (m.find()) throw new EmillaException(errorTitle, R.string.error_excess_time_units);
        }

        m = yearRegex().matcher(s);
        if (m.find()) {
            String y = m.group();
            if (m.find()) throw new EmillaException(errorTitle, R.string.error_excess_time_units);

            int tickIndex = y.indexOf('\'');
            if (tickIndex == -1) cal.set(YEAR, Integer.parseInt(y));
            else cal.set(YEAR, cal.get(YEAR) / 100 * 100 + Integer.parseInt(y.substring(tickIndex + 1)));
        }

        return cal;
    }

    public static long[] parseDateAndTimes(String date, @StringRes int errorTitle) {
        String[] dateTime = date.split(" *@ *|(^| +)(at|from) +");

        int[] startTime = null;
        int[] endTime = null;
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
                endTime = parseTime(endStr, null, errorTitle);
                // fallthrough
            case 1:
                startTime = parseTime(times[0], null, errorTitle);
                break;
            default:
                throw new EmillaException(errorTitle, R.string.error_excess_timespan);
            }
        } else if (len != 1) {
            throw new EmillaException(errorTitle, R.string.error_excess_timespans);
        }

        Calendar cal = parseDate(date, errorTitle);
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

    private Time() {}
}
