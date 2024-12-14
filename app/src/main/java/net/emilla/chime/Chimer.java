package net.emilla.chime;

public interface Chimer {
    byte // Chime IDs
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

    void chime(byte id);
}
