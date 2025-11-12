package net.emilla.lang.date;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.stream.Collectors;

public final class Weekdays {

    public static ArrayList<Integer> calendarArrayList(Collection<DayOfWeek> weekdays) {
        return weekdays.stream()
            .map(Weekdays::toCalendarConstant)
            .collect(Collectors.toCollection(() -> new ArrayList<Integer>(weekdays.size())));
    }

    private static int toCalendarConstant(DayOfWeek weekday) {
        // DayOfWeek starts on Monday while the calendar constants start on Sunday
        return switch (weekday) {
            case MONDAY -> Calendar.MONDAY;
            case TUESDAY -> Calendar.TUESDAY;
            case WEDNESDAY -> Calendar.WEDNESDAY;
            case THURSDAY -> Calendar.THURSDAY;
            case FRIDAY -> Calendar.FRIDAY;
            case SATURDAY -> Calendar.SATURDAY;
            case SUNDAY -> Calendar.SUNDAY;
        };
    }

    private Weekdays() {}

}
