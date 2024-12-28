package net.emilla.settings;

import static net.emilla.command.EmillaCommand.Commands.*;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.action.AssistantSettings;
import net.emilla.action.Flashlight;
import net.emilla.action.NoAction;
import net.emilla.action.QuickAction;
import net.emilla.action.SelectAll;
import net.emilla.chime.Chimer;
import net.emilla.utils.Features;

public class SettingVals {

    public static String soundSet(SharedPreferences prefs) {
        return prefs.getString(Chimer.SOUND_SET, Chimer.NEBULA);
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
            default /*"always"*/ -> true;
        };
    }

    public static boolean alwaysShowData(SharedPreferences prefs) {
        return prefs.getBoolean("always_show_data", false);
    }

    public static QuickAction noCommand(SharedPreferences prefs, AssistActivity act) {
        return quickAction(prefs, QuickAction.PREF_NO_COMMAND, QuickAction.ASSISTANT_SETTINGS, act);
    }

    public static QuickAction longSubmit(SharedPreferences prefs, AssistActivity act) {
        return quickAction(prefs, QuickAction.PREF_LONG_SUBMIT, QuickAction.SELECT_ALL, act);
    }

    public static QuickAction doubleAssist(SharedPreferences prefs, AssistActivity act,
            PackageManager pm) {
        String defaultAction = Features.torch(pm) ? QuickAction.FLASHLIGHT
                : QuickAction.ASSISTANT_SETTINGS;
        return quickAction(prefs, QuickAction.PREF_DOUBLE_ASSIST, defaultAction, act);
    }

    public static QuickAction menuAction(SharedPreferences prefs, AssistActivity act) {
        return quickAction(prefs, QuickAction.PREF_MENU_KEY, QuickAction.ASSISTANT_SETTINGS, act);
    }

    private static QuickAction quickAction(SharedPreferences prefs, String actionPref,
            String defaultAction, AssistActivity act) {
        return switch (prefs.getString(actionPref, defaultAction)) {
            case QuickAction.FLASHLIGHT -> new Flashlight(act);
            case QuickAction.ASSISTANT_SETTINGS -> new AssistantSettings(act);
            case QuickAction.SELECT_ALL -> new SelectAll(act);
            default -> new NoAction(act);
        };
    }
}
