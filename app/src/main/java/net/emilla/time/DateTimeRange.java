package net.emilla.time;

import androidx.annotation.Nullable;

import java.time.LocalDateTime;

record DateTimeRange(LocalDateTime start, @Nullable LocalDateTime end) {
    DateTimeRange {
        if (end != null && end.isBefore(start)) {
            throw new IllegalArgumentException("Timespan ends before it starts");
        }
    }
}
