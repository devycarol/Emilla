package net.emilla.time;

import androidx.annotation.Nullable;

import net.emilla.annotation.Normalized;
import net.emilla.lang.Lang;

enum KnownCity {
    ANCHORAGE(TimeZone.ALASKA),

    LOS_ANGELES(TimeZone.PACIFIC),
    SEATTLE(TimeZone.PACIFIC),
    PORTLAND(TimeZone.PACIFIC),

    PHOENIX(TimeZone.ARIZONA),

    DENVER(TimeZone.MOUNTAIN),
    BOISE(TimeZone.MOUNTAIN),

    CHICAGO(TimeZone.CENTRAL),

    NEW_YORK(TimeZone.EASTERN),
    WASHINGTON(TimeZone.EASTERN),
;
    public final TimeZone timeZone;

    KnownCity(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    @Nullable
    public static KnownCity of(Lang lang, @Normalized String name) {
        return switch (lang) {
            case EN_US -> EnglishUS.knownCity(name);
        };
    }
}
