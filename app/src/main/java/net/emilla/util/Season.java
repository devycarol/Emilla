package net.emilla.util;

import java.time.Month;

enum Season {
    WINTER,
    SPRING,
    SUMMER,
    FALL,
;
    // todo: change the app icon depending on the season
    public static Season of(Month month, boolean isSouthernHemisphere) {
        int season = month.ordinal();
        ++season;
        if (season == 12) {
            season = 0;
        }
        season /= 3;
        if (isSouthernHemisphere) {
            season += 2;
            if (season >= 4) {
                season -= 4;
            }
        }
        return values()[season];
    }
}
