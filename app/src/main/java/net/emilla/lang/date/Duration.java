package net.emilla.lang.date;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.exception.EmillaException;

public final class Duration {

    public final int seconds;

    public Duration(int seconds, @StringRes int errorTitle) {
        if (seconds <= 0) throw new EmillaException(errorTitle, R.string.error_bad_minutes);
        this.seconds = seconds;
    }
}
