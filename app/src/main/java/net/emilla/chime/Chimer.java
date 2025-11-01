package net.emilla.chime;

import android.content.Context;
import android.content.SharedPreferences;

import net.emilla.config.SettingVals;

@FunctionalInterface
public interface Chimer {

    // Preference IDs
    String SILENCE = "none";
    String NEBULA = "nebula";
    String REDIAL = "voice_dialer";
    String CUSTOM = "custom";

    /// The user's preferred chimer for audio feedback.
    ///
    /// @param prefs used to build the chimer from user settings.
    /// @return the user's chosen chimer.
    static Chimer of(SharedPreferences prefs) {
        return switch (SettingVals.chimerId(prefs)) {
            case SILENCE -> new Silence();
            case NEBULA -> new Nebula();
            case REDIAL -> new Redial();
            case CUSTOM -> new Custom(prefs);
            default -> throw new IllegalArgumentException();
        };
    }

    void chime(Context ctx, Chime chime);

}
