package net.emilla.chime;

public interface Chimer {

    byte // IDs
            START = 0,
            ACT = 1,
            PEND = 2,
            RESUME = 3,
            EXIT = 4,
            SUCCEED = 5,
            FAIL = 6;

    String // Sound sets
            NONE = "none",
            NEBULA = "nebula",
            VOICE_DIALER = "voice_dialer",
            CUSTOM = "custom";

    String // Preference keys
            SOUND_SET = "sound_set",
            PREF_START = "chime_start",
            PREF_ACT = "chime_act",
            PREF_PEND = "chime_pend",
            PREF_RESUME = "chime_resume",
            PREF_EXIT = "chime_exit",
            PREF_SUCCEED = "chime_succeed",
            PREF_FAIL = "chime_fail";

    void chime(byte id);
}
