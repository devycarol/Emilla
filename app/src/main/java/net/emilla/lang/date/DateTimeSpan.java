package net.emilla.lang.date;

import androidx.annotation.Nullable;

import java.time.LocalDateTime;

public final class DateTimeSpan {

    public final LocalDateTime start;
    @Nullable
    public final LocalDateTime end;

    public DateTimeSpan(LocalDateTime start, @Nullable LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

}
