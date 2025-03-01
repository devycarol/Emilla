package net.emilla.lang.phrase;

import static java.lang.Math.min;

import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;

public record RandRange(int inclusStart, int exclusEnd) {

    public RandRange(int inclusEnd) {
        this(min(inclusEnd, 1), inclusEnd <= 0 ? 0 : inclusEnd + 1);
    }

    public RandRange {
        if (inclusStart >= exclusEnd) {
            throw new EmlaBadCommandException(R.string.command_random_number,
                                              R.string.error_invalid_number_range);
        }
    }
}
