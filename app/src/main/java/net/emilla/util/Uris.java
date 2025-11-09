package net.emilla.util;

import android.net.Uri;

public final class Uris {

    public static Uri sms(String numbersCsv) {
        return Uri.parse("smsto:" + numbersCsv);
    }

    private Uris() {}

}
