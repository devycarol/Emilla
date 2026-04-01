package net.emilla.lang.date.impl;

import androidx.annotation.Nullable;

public enum DurationEN_US {;
    @Nullable
    public static Integer seconds(String minutes) {
        try {
            // Todo: other time units, clock notation.
            var seconds = (int) (Double.parseDouble(minutes) * 60.0);
            if (seconds <= 0) {
                return null;
            }

            return seconds;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
