package net.emilla.utils;

import static android.provider.CalendarContract.Events.*;

import net.emilla.exceptions.EmlaBadCommandException;

public class CalendarDetails {
    public static int parseAvailability(String s) {
        return switch (s.trim().toLowerCase()) {
            case "busy" -> AVAILABILITY_BUSY;
            case "free", "available", "avail" -> AVAILABILITY_FREE;
            case "tentative", "tent" -> AVAILABILITY_TENTATIVE;
            default -> throw new EmlaBadCommandException("Only busy, free/avail|able, and tent|ative availabilities are allowed.");
        };
    }

    public static int parseVisibility(String s) {
        return switch (s.trim().toLowerCase()) {
            case "confidential", "confid", "conf", "con" -> ACCESS_CONFIDENTIAL;
            case "private", "priv", "pri" -> ACCESS_PRIVATE;
            case "public", "pub" -> ACCESS_PUBLIC;
            default -> throw new EmlaBadCommandException("Only pub|lic, pri|v|ate, and con|f|idential visibilities are allowed.");
        };
    }
}
