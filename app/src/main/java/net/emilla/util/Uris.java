package net.emilla.util;

import android.net.Uri;

public enum Uris {
    ;

    public static Uri sms(String numbersCsv) {
        return Uri.parse("smsto:" + numbersCsv);
    }

}
