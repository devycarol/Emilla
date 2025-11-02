package net.emilla.config;

import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;

import net.emilla.command.app.AppEntry;
import net.emilla.command.app.AppProperties;

import java.util.Set;

public final class Aliases {

    @Nullable
    public static Set<String> appSet(SharedPreferences prefs, Resources res, AppEntry app) {
        String entry = app.entry();

        AppProperties properties = app.properties;
        if (properties != null) {
            return coreSet(prefs, res, entry, properties.aliases);
        }

        return prefs.getStringSet(setKey(entry), null);
    }

    @Nullable
    public static Set<String> coreSet(
        SharedPreferences prefs,
        Resources res,
        String entry,
        @ArrayRes int setId
    ) {
        Set<String> set = prefs.getStringSet(
            setKey(entry),
            Set.of(res.getStringArray(setId))
        );
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
