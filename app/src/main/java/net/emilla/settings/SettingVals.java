package net.emilla.settings;

import static net.emilla.command.EmillaCommand.*;

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
import net.emilla.command.core.Alarm;
import net.emilla.command.core.Bookmark;
import net.emilla.command.core.Calculate;
import net.emilla.command.core.Calendar;
import net.emilla.command.core.Call;
import net.emilla.command.core.Contact;
import net.emilla.command.core.Copy;
import net.emilla.command.core.Dial;
import net.emilla.command.core.Email;
import net.emilla.command.core.Info;
import net.emilla.command.core.Launch;
import net.emilla.command.core.Navigate;
import net.emilla.command.core.Pomodoro;
import net.emilla.command.core.Settings;
import net.emilla.command.core.Share;
import net.emilla.command.core.Sms;
import net.emilla.command.core.Time;
import net.emilla.command.core.Timer;
import net.emilla.command.core.Toast;
import net.emilla.command.core.Torch;
import net.emilla.command.core.Uninstall;
import net.emilla.command.core.Weather;
import net.emilla.command.core.Web;
import net.emilla.utils.Features;

public class SettingVals {

    public static String soundSet(SharedPreferences prefs) {
        return prefs.getString(Chimer.SOUND_SET, Chimer.NEBULA);
    }

    private static short cmdId(String entry) {
        return switch (entry) {
            case Call.ENTRY -> CALL;
            case Dial.ENTRY -> DIAL;
            case Sms.ENTRY -> SMS;
            case Email.ENTRY -> EMAIL;
            case Navigate.ENTRY -> NAVIGATE;
            case Launch.ENTRY -> LAUNCH;
            case Copy.ENTRY -> COPY;
            case Share.ENTRY -> SHARE;
            case Settings.ENTRY -> SETTINGS;
        //    case Note.ENTRY -> NOTE;
        //    case Todo.ENTRY -> TODO;
            case Web.ENTRY -> WEB;
        //    case Find.ENTRY -> FIND;
            case Time.ENTRY -> TIME;
            case Alarm.ENTRY -> ALARM;
            case Timer.ENTRY -> TIMER;
            case Pomodoro.ENTRY -> POMODORO;
            case Calendar.ENTRY -> CALENDAR;
            case Contact.ENTRY -> CONTACT;
        //    case Notify.ENTRY -> NOTIFY;
            case Calculate.ENTRY -> CALCULATE;
            case Weather.ENTRY -> WEATHER;
            case Bookmark.ENTRY -> BOOKMARK;
            case Torch.ENTRY -> TORCH;
            case Info.ENTRY -> INFO;
            case Uninstall.ENTRY -> UNINSTALL;
            case Toast.ENTRY -> TOAST;
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
