package net.emilla.lang.date;

import androidx.annotation.Nullable;

import java.time.LocalDateTime;

public record DateTimeRange(LocalDateTime start, @Nullable LocalDateTime end) {
}
