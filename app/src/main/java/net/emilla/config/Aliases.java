package net.emilla.config;

import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;

import net.emilla.command.app.AppEntry;
import net.emilla.command.app.AppProperties;
import net.emilla.command.core.CoreEntry;

import java.util.Locale;
import java.util.Set;

public enum Aliases {
    ;

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
        String[] aliases = res.getStringArray(setId);
        return prefs.getStringSet(
            setKey(entry),
            aliases.length > 0 ? Set.of(aliases) : null
        );
    }

    public static String setKey(String entry) {
        return "aliases_" + entry;
    }

    public static String textKey(String entry) {
        return "aliases_" + entry + "_text";
    }

    @Deprecated
    public static void reformatCoresIfNecessary(SharedPreferences prefs) {
        if (prefs.contains("aliases_web")) {
            SharedPreferences.Editor edit = prefs.edit();

            String defaultCommand = prefs.getString(SettingVals.DEFAULT_COMMAND, CoreEntry.WEB.name());
            if (defaultCommand.equals(defaultCommand.toLowerCase(Locale.ROOT))) {
                edit.putString(SettingVals.DEFAULT_COMMAND, defaultCommand.toUpperCase(Locale.ROOT));
            }

            for (var coreEntry : CoreEntry.values()) {
                String newEntry = coreEntry.name();
                String newEnabledKey = SettingVals.commandEnabledKey(newEntry);
                String newSetKey = setKey(newEntry);
                String newTextKey = textKey(newEntry);

                String oldEntry = newEntry.toLowerCase(Locale.ROOT);
                String oldEnabledKey = SettingVals.commandEnabledKey(oldEntry);
                String oldSetKey = setKey(oldEntry);
                String oldTextKey = textKey(oldEntry);

                if (prefs.contains(oldEnabledKey)) {
                    edit.putBoolean(newEnabledKey, prefs.getBoolean(oldEnabledKey, true))
                        .remove(oldEnabledKey);
                }

                if (prefs.contains(oldSetKey)) {
                    edit.putStringSet(newSetKey, prefs.getStringSet(oldSetKey, null))
                        .remove(oldSetKey);
                }

                if (prefs.contains(oldTextKey)) {
                    edit.putString(newTextKey, prefs.getString(oldTextKey, null))
                        .remove(oldTextKey);
                }
            }

            edit.apply();
        }
    }

}
