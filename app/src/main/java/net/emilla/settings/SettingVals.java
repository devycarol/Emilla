package net.emilla.settings;

import static net.emilla.command.EmillaCommand.Commands.*;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

import net.emilla.R;
import net.emilla.chime.Chimer;

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
public static String soundSet(SharedPreferences prefs) {
    return prefs.getString(SOUND_SET, Chimer.NEBULA);
}

private static short cmdId(String s) {
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
    case "torch" -> TORCH;
    case "info" -> INFO;
    case "uninstall" -> UNINSTALL;
    case "toast" -> TOAST;
    default -> 0;
    };
}

public static short defaultCommand(SharedPreferences prefs) {
    return cmdId(prefs.getString("default_command", "web"));
}

public static boolean showTitleBar(SharedPreferences prefs, Resources res) {
    return switch (prefs.getString("show_titlebar", res.getString(R.string.conf_show_titlebar))) {
        // Todo: in the off chance app-data is transferred across phone/tablet, the setting
        //  should change if it's still default. back:prefs.xml
        case "never" -> false;
        case "portrait" -> {
            DisplayMetrics dm = res.getDisplayMetrics();
            yield dm.widthPixels < dm.heightPixels;
        }
        default /*always*/ -> true;
    };
}

public static boolean alwaysShowData(SharedPreferences prefs) {
    return prefs.getBoolean("always_show_data", false);
}
}
