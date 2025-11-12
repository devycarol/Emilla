package net.emilla.lang.date;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.exception.EmillaException;
import net.emilla.util.Strings;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
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

    private static final String TOMORROW = "tomorrow";

    public static final Pattern REGEX_AM = Pattern.compile("\\d *A", Pattern.CASE_INSENSITIVE);
    public static final Pattern REGEX_PM = Pattern.compile("\\d *P", Pattern.CASE_INSENSITIVE);
    private static final Pattern MERIDIEM = Pattern.compile("\\d *[AP]", Pattern.CASE_INSENSITIVE);

    private static final Pattern DURATION_SPLITTER = Pattern.compile(" *: *| +");

    private static final Pattern DURATION_UNTIL = Pattern.compile(
        "(until|t(ill?|o)) .*",
        Pattern.CASE_INSENSITIVE
    );
    private static final Pattern HOURS = Pattern.compile(
        "\\d*\\.?\\d+ *h((ou)?rs?)?",
        Pattern.CASE_INSENSITIVE
    );
    private static final Pattern MINUTES = Pattern.compile(
        "\\d*\\.?\\d+ *m(in(ute)?s?)?",
        Pattern.CASE_INSENSITIVE
    );
    private static final Pattern SECONDS = Pattern.compile(
        "\\d*\\.?\\d+ *s(ec(ond)?s?)?",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern REGEX_MONTH = Pattern.compile(
        String.format(
            "(%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s)",
            JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC
        ),
        Pattern.CASE_INSENSITIVE
    );
    private static final Pattern REGEX_DAY = Pattern.compile("(^| +)\\d?\\d( +|$)");
    private static final Pattern REGEX_YEAR = Pattern.compile("(\\d\\d|')\\d\\d");

    private static final Pattern DATE_TIME_SPLITTER = Pattern.compile(
        " *@ *|(^| +)(at|from) +",
        Pattern.CASE_INSENSITIVE
    );
    private static final Pattern TIME_SPAN_SPLITTER = Pattern.compile(
        " *(-|t(o|ill?)|until) *",
        Pattern.CASE_INSENSITIVE
    );
    private static final Pattern SPACE_TOMORROW = Pattern.compile(
        " *" + TOMORROW,
        Pattern.CASE_INSENSITIVE
    );

    private static int[] timeUnits(String time, @StringRes int errorTitle) {
        String timeDigits = Strings.stripNonDigits(time);
        if (!Strings.isOneToNDigits(timeDigits, 6)) {
            throw new EmillaException(errorTitle, R.string.error_invalid_time);
        }

        int h = 0;
        int m = 0;
        int s = 0;
        int len = timeDigits.length();
        switch ((len + 1) / 2) {
        case 1 -> {
            h = Integer.parseInt(timeDigits);
        }
        case 2 -> {
            h = Integer.parseInt(timeDigits.substring(0, len - 2));
            m = Integer.parseInt(timeDigits.substring(len - 2));
        }
        case 3 -> {
            h = Integer.parseInt(timeDigits.substring(0, len - 4));
            m = Integer.parseInt(timeDigits.substring(len - 4, len - 2));
            s = Integer.parseInt(timeDigits.substring(len - 2));
        }}

        return new int[]{h, m, s};
    }

    private static int[] parseTime(String time, @StringRes int errorTitle) {
        var meridiem
            = REGEX_AM.matcher(time).find() ? Meridiem.AM
            : REGEX_PM.matcher(time).find() ? Meridiem.PM
            : Meridiem.UNSPECIFIED;

        int[] units = timeUnits(time, errorTitle);
        int h = units[0];
        int m = units[1];
        int s = units[2];

        if (meridiem != Meridiem.UNSPECIFIED) {
            if (h < 1 || 12 < h) throw new EmillaException(errorTitle, R.string.error_invalid_time);
            if (h == 12) h = 0;
            if (meridiem == Meridiem.PM) h += 12;
        }

        if (h > 23 || Math.max(m, s) > 59) {
            throw new EmillaException(errorTitle, R.string.error_invalid_time);
        }

        return new int[]{h, m, s};
    }

    private static Duration parseDurationUntil(String until, @StringRes int errorTitle) {
        int[] endUnits = parseTime(until, errorTitle);
        int endHour = endUnits[0];
        int endMinute = endUnits[1];
        int endSecond = endUnits[2];

        var timeNow = LocalTime.now();
        int hour24Now = timeNow.getHour();
        int minuteNow = timeNow.getMinute();
        int secondNow = timeNow.getSecond();

        int h = 0;
        int m = 0;
        int s = 0;

        s = endSecond - secondNow;
        if (s < 0) {
            s += 60;
            --m;
        }

        m += endMinute - minuteNow;
        if (m < 0) {
            m += 60;
            --h;
        }

        h += endHour - hour24Now;
        if (h < 0) {
            h += 24;
        }

        if (!MERIDIEM.matcher(until).find()) {
            if (h > 12 || h == 12 && Math.max(m, s) > 0) {
                h -= 12;
            }
        }

        return new Duration(h, m, s - 1);
        // we subtract 1 to give an extra second of leeway
    }

    private static Duration splitDurationString(String dur) {
        String[] units = DURATION_SPLITTER.split(dur);

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
        if (DURATION_UNTIL.matcher(dur).find()) {
            return parseDurationUntil(dur, errorTitle);
        }

        var timeUnits = new double[3];
        Pattern[] patterns = {HOURS, MINUTES, SECONDS};

        boolean hit = false;
        boolean notEmpty = true;
        for (int i = 0; i < 3 && notEmpty; ++i) {
            Pattern pattern = patterns[i];
            if (pattern.matcher(dur).find()) {
                hit = true;

                String[] withoutHours = pattern.split(dur);
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

                timeUnits[i] = Double.parseDouble(Strings.stripNonNumbers(dur.substring(start, end)));
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

    private static Month parseMonth(String month) {
        return switch (month.substring(0, 3).toLowerCase()) {
            case "jan" -> Month.JANUARY;
            case "feb" -> Month.FEBRUARY;
            case "mar" -> Month.MARCH;
            case "apr" -> Month.APRIL;
            case "may" -> Month.MAY;
            case "jun" -> Month.JUNE;
            case "jul" -> Month.JULY;
            case "aug" -> Month.AUGUST;
            case "sep" -> Month.SEPTEMBER;
            case "oct" -> Month.OCTOBER;
            case "nov" -> Month.NOVEMBER;
            case "dec" -> Month.DECEMBER;
            default -> throw new IllegalArgumentException("Invalid month: " + month);
        };
    }

    private static LocalDateTime parseDateToNextHalfHour(String s, @StringRes int errorTitle) {
        // TODO more formats but dear god locales ughhhhh
        var timeNow = LocalTime.now();
        var minutesToNextHalfHour = (long) -timeNow.getMinute() % 30L + 30L;
        LocalTime nextHalfHour = timeNow.plusMinutes(minutesToNextHalfHour)
            .withSecond(0)
            .withNano(0);

        var dateNow = LocalDate.now();
        var date = dateNow;

        if (s.equalsIgnoreCase(TOMORROW)) {
            LocalDate tomorrow = date.plusDays(1L);
            return LocalDateTime.of(tomorrow, nextHalfHour);
        }

        Matcher monthMatcher = REGEX_MONTH.matcher(s);
        if (monthMatcher.find()) {
            Month month = parseMonth(monthMatcher.group());
            date = date.withMonth(month.getValue());
            if (monthMatcher.find()) {
                throw new EmillaException(errorTitle, R.string.error_excess_time_units);
            }
        }

        Matcher dayMatcher = REGEX_DAY.matcher(s);
        if (dayMatcher.find()) {
            // todo: this shouldn't work for just a day-number
            int dayOfMonth = Integer.parseInt(dayMatcher.group().trim());
            date = date.withDayOfMonth(dayOfMonth);
            if (dayMatcher.find()) {
                throw new EmillaException(errorTitle, R.string.error_excess_time_units);
            }
        }

        Matcher yearMatcher = REGEX_YEAR.matcher(s);
        if (yearMatcher.find()) {
            String yearString = yearMatcher.group();
            if (yearMatcher.find()) {
                throw new EmillaException(errorTitle, R.string.error_excess_time_units);
            }

            int tickIndex = yearString.indexOf('\'');
            if (tickIndex >= 0) {
                int startOfCentury = date.getYear() / 100 * 100;
                int yearOfCentury = Integer.parseInt(yearString.substring(tickIndex + 1));
                date = date.withYear(startOfCentury + yearOfCentury);
            } else {
                int year = Integer.parseInt(yearString);
                date = date.withYear(year);
            }
        } else if (date.isBefore(dateNow)) {
            date = date.plusYears(1L);
        } else if (date.equals(dateNow)) {
            throw new EmillaException(errorTitle, R.string.error_invalid_date);
        }

        return LocalDateTime.of(date, nextHalfHour);
    }

    public static DateTimeSpan parseDateAndTimes(String dateString, @StringRes int errorTitle) {
        String[] dateTime = DATE_TIME_SPLITTER.split(dateString);

        int[] startTime = null;
        int[] endTime = null;
        boolean endTomorrow = false; // TODO
        int len = dateTime.length;
        if (len == 2) {
            dateString = dateTime[0];
            String[] times = TIME_SPAN_SPLITTER.split(dateTime[1]);

            switch (times.length) {
            case 2:
                String endStr = times[1];
                if (Strings.containsIgnoreCase(endStr, TOMORROW)) {
                    endTomorrow = true;
                    endStr = SPACE_TOMORROW.matcher(endStr).replaceFirst("");
                } else if (Strings.containsIgnoreCase(endStr, "on ")) {
                    // todo
                }
                endTime = parseTime(endStr, errorTitle);
                // fallthrough
            case 1:
                startTime = parseTime(times[0], errorTitle);
                break;
            default:
                throw new EmillaException(errorTitle, R.string.error_excess_timespan);
            }
        } else if (len != 1) {
            throw new EmillaException(errorTitle, R.string.error_excess_timespans);
        }

        LocalDateTime startDate = parseDateToNextHalfHour(dateString, errorTitle);
        LocalDateTime endDate = null;
        if (startTime != null) {
            if (endTime != null) {
                endDate = LocalDateTime.of(
                    startDate.toLocalDate(),
                    LocalTime.of(endTime[0], endTime[1], endTime[2])
                );
            }

            startDate = LocalDateTime.of(
                startDate.toLocalDate(),
                LocalTime.of(startTime[0], startTime[1], startTime[2])
            );
        }

        return new DateTimeSpan(startDate, endDate);
    }

    private Time() {}
}
