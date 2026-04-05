package net.emilla.time;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.annotation.open;
import net.emilla.lang.Lang;

import java.time.ZoneId;

public enum TimeZone {
    LOCAL(null, R.string.time_zone_local) {
        @Override
        public ZoneId id() {
            return ZoneId.systemDefault();
        }
    },
    // Todo: but all of them tho
    ALASKA(ZoneId.of("America/Anchorage"), R.string.time_zone_alaska),
    PACIFIC(ZoneId.of("America/Los_Angeles"), R.string.time_zone_pacific),
    ARIZONA(ZoneId.of("America/Phoenix"), R.string.time_zone_arizona),
    MOUNTAIN(ZoneId.of("America/Denver"), R.string.time_zone_mountain),
    CENTRAL(ZoneId.of("America/Chicago"), R.string.time_zone_central),
    EASTERN(ZoneId.of("America/New_York"), R.string.time_zone_eastern),
;
    private final ZoneId mId;
    @StringRes
    public final int name;

    TimeZone(ZoneId id, @StringRes int name) {
        mId = id;
        this.name = name;
    }

    @Nullable
    public static TimeZone of(Lang lang, String location) {
        location = Lang.normalize(location);
        var city = KnownCity.of(lang, location);
        if (city != null) {
            return city.timeZone;
        }

        return switch (lang) {
            case EN_US -> EnglishUS.timeZone(location);
        };
    }

    public @open ZoneId id() {
        return mId;
    }
}
