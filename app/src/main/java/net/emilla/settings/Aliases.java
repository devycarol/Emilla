package net.emilla.settings;

import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;

import java.util.Set;

public final class Aliases {

    public static String textKey(String entry) {
        return "aliases_" + entry + "_text";
    }

    public static Set<String> coreSet(
        SharedPreferences prefs,
        Resources res,
        String commandEntry,
        @ArrayRes int aliases
    ) {
        return prefs.getStringSet("aliases_" + commandEntry, Set.of(res.getStringArray(aliases)));
    }

    @Nullable
    public static Set<String> appSet(
        SharedPreferences prefs,
        Resources res,
        String pkg,
        @ArrayRes int setId
    ) {
        if (setId == 0) return null;
        return prefs.getStringSet("aliases_" + pkg, Set.of(res.getStringArray(setId)));
    }
}
