package net.emilla.settings;

import static net.emilla.commands.EmillaCommand.Commands.*;

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

private static short commandId(final String s) {
    return switch (s) {
    case "call" -> CALL;
    case "dial" -> DIAL;
    case "sms" -> SMS;
    case "email" -> EMAIL;
    case "launch" -> LAUNCH;
    case "share" -> SHARE;
    case "settings" -> SETTINGS;
//    case "note" -> NOTE;
//    case "todo" -> TODO;
    case "web" -> WEB;
//    case "find" -> FIND;
    case "clock" -> CLOCK;
    case "alarm" -> ALARM;
    case "timer" -> TIMER;
    case "pomodoro" -> POMODORO;
    case "calendar" -> CALENDAR;
    case "contact" -> CONTACT;
//    case "notify" -> NOTIFY;
    case "calculate" -> CALCULATE;
    case "weather" -> WEATHER;
    case "view" -> VIEW;
    case "info" -> INFO;
    case "toast" -> TOAST;
    default -> 0;
    };
}

public static short defaultCommand(final SharedPreferences prefs) {
    return commandId(prefs.getString("default_command", "web"));
}
}
