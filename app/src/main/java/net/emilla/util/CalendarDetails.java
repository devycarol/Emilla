package net.emilla.util;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.exception.EmillaException;

public final class CalendarDetails {

    public static int parseAvailability(String s, @StringRes int errorTitle) {
        throw new EmillaException(errorTitle, R.string.error_unfinished_feature);
    }

    public static int parseVisibility(String s, @StringRes int errorTitle) {
        throw new EmillaException(errorTitle, R.string.error_unfinished_feature);
    }

    private CalendarDetails() {}
}
