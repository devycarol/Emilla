package net.emilla.time;

import androidx.annotation.Nullable;

import net.emilla.annotation.Normalized;

enum EnglishUS {;
    @Nullable
    public static KnownCity knownCity(@Normalized String name) {
        return switch (name) {
            case "anchorage" -> KnownCity.ANCHORAGE;
            case "los angeles", "la" -> KnownCity.LOS_ANGELES;
            case "seattle" -> KnownCity.SEATTLE;
            case "portland" -> KnownCity.PORTLAND;
            case "denver" -> KnownCity.DENVER;
            case "boise" -> KnownCity.BOISE;
            case "phoenix" -> KnownCity.PHOENIX;
            case "chicago" -> KnownCity.CHICAGO;
            case "new york", "nyc" -> KnownCity.NEW_YORK;
            case "washington d.c.", "washington dc", "dc" -> KnownCity.WASHINGTON;
            default -> null;
        };
    }

    @Nullable
    public static TimeZone timeZone(@Normalized String name) {
        return switch (name) {
            case "alaska" -> TimeZone.ALASKA;
            case "pacific" -> TimeZone.PACIFIC;
            case "mountain" -> TimeZone.MOUNTAIN;
            case "arizona" -> TimeZone.ARIZONA;
            case "central" -> TimeZone.CENTRAL;
            case "eastern" -> TimeZone.EASTERN;
            default -> null;
        };
    }
}
