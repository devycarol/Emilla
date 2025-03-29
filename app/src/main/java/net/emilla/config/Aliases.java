package net.emilla.config;

import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;

import java.util.Set;

public final class Aliases {

    @Nullable
    public static Set<String> appSet(
        SharedPreferences prefs,
        Resources res,
        String entry,
        @ArrayRes int setId
    ) {
        return setId == 0 ? prefs.getStringSet(setKey(entry), null)
                          : coreSet(prefs, res, entry, setId);
    }

    @Nullable
    public static Set<String> coreSet(
        SharedPreferences prefs,
        Resources res,
        String entry,
        @ArrayRes int setId
    ) {
        Set<String> set = Set.of(res.getStringArray(setId));
        set = prefs.getStringSet(setKey(entry), set);
        return set.isEmpty() ? null : set;
    }

    public static String setKey(String entry) {
        return "aliases_" + entry;
    }

    public static String textKey(String entry) {
        return "aliases_" + entry + "_text";
    }

    private Aliases() {}
}
