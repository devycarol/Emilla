package net.emilla.lang.date.impl;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.exception.EmillaException;
import net.emilla.lang.date.Duration;

public final class DurationEN_US {

    public static Duration instance(String minutes, @StringRes int errorTitle) {
        try {
            var seconds = (int) (Double.parseDouble(minutes) * 60.0);
            // Todo: other time units, clock notation.
            return new Duration(seconds, errorTitle);
        } catch (NumberFormatException e) {
            throw new EmillaException(errorTitle, R.string.error_bad_minutes);
        }
    }

    private DurationEN_US() {}
}
