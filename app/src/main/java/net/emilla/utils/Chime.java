package net.emilla.utils;

import android.media.ToneGenerator;

import androidx.annotation.RawRes;

import net.emilla.R;
import net.emilla.settings.SettingVals;

public class Chime {
public static final byte // Chime IDs
    START = 0,
    ACT = 1,
    PEND = 2,
    RESUME = 3,
    EXIT = 4,
    SUCCEED = 5,
    FAIL = 6;
public static final String // Sound sets
    NONE = "none",
    NEBULA = "nebula",
    VOICE_DIALER = "voice_dialer",
    CUSTOM = "custom";

@RawRes
public static int nebula(byte chime) {
    return switch (chime) {
    case START -> R.raw.nebula_start;
    case ACT -> R.raw.nebula_act;
    case PEND -> R.raw.nebula_pend;
    case RESUME -> R.raw.nebula_resume;
    case EXIT -> R.raw.nebula_exit;
    case SUCCEED -> R.raw.nebula_succeed;
    case FAIL -> R.raw.nebula_fail;
    default -> -1;
    };
}

public static int dialerTone(byte chime) {
    return switch (chime) {
        case START, PEND, RESUME -> ToneGenerator.TONE_PROP_BEEP;
        case ACT -> ToneGenerator.TONE_PROP_PROMPT;
        case EXIT -> ToneGenerator.TONE_PROP_BEEP2;
        case SUCCEED -> ToneGenerator.TONE_PROP_ACK;
        case FAIL -> ToneGenerator.TONE_PROP_NACK;
        default -> -1;
    };
}

public static String preferenceOf(byte chime) {
    return switch(chime) {
    case START -> SettingVals.CHIME_START;
    case ACT -> SettingVals.CHIME_ACT;
    case PEND -> SettingVals.CHIME_PEND;
    case RESUME -> SettingVals.CHIME_RESUME;
    case EXIT -> SettingVals.CHIME_EXIT;
    case SUCCEED -> SettingVals.CHIME_SUCCEED;
    case FAIL -> SettingVals.CHIME_FAIL;
    default -> null;
    };
}

private Chime() {}
}
