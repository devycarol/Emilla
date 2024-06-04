package net.emilla.settings;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import net.emilla.utils.Chime;

public class SettingVals {
public static final String // Preference keys
    SOUND_SET = "sound_set",
    CHIME_START = "chime_start",
    CHIME_ACT = "chime_act",
    CHIME_PEND = "chime_pend",
    CHIME_RESUME = "chime_resume",
    CHIME_EXIT = "chime_exit",
    CHIME_SUCCEED = "chime_succeed",
    CHIME_FAIL = "chime_fail";

@NonNull
public static String soundSet(final SharedPreferences prefs) {
    return prefs.getString(SOUND_SET, Chime.NEBULA);
}
}
