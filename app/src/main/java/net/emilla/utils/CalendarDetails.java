package net.emilla.utils;

import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;

public final class CalendarDetails {

    public static int parseAvailability(String s) {
        throw new EmlaBadCommandException(R.string.command_calendar, R.string.error_unfinished_feature);
    }

    public static int parseVisibility(String s) {
        throw new EmlaBadCommandException(R.string.command_calendar, R.string.error_unfinished_feature);
    }

    private CalendarDetails() {}
}
