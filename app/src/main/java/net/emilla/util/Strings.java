package net.emilla.util;

import androidx.annotation.Nullable;

public enum Strings {;
    public static String emptyIfNull(@Nullable String s) {
        return s != null
            ? s
            : ""
        ;
    }
}
