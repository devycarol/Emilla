package net.emilla.lang.phrase;

import static java.lang.Math.min;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.exception.EmillaException;

public final class RandRange {

    public final int inclusStart;
    public final int exclusEnd;

    public RandRange(int inclusEnd, @StringRes int errorTitle) {
        this(min(inclusEnd, 1), inclusEnd <= 0 ? 0 : inclusEnd + 1, errorTitle);
    }

    public RandRange(int inclusStart, int exclusEnd, @StringRes int errorTitle) {
        if (inclusStart >= exclusEnd) {
            throw new EmillaException(errorTitle, R.string.error_invalid_number_range);
        }

        this.inclusStart = inclusStart;
        this.exclusEnd = exclusEnd;
    }
}
