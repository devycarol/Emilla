package net.emilla.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.action.AssistantSettings;
import net.emilla.action.Flashlight;
import net.emilla.action.Help;
import net.emilla.action.NoAction;
import net.emilla.action.QuickAction;
import net.emilla.action.SelectAll;
import net.emilla.chime.Chimer;
import net.emilla.chime.Custom;
import net.emilla.chime.Nebula;
import net.emilla.chime.Redial;
import net.emilla.chime.Silence;
import net.emilla.command.DefaultCommandWrapper;
import net.emilla.command.core.Alarm;
import net.emilla.command.core.Bookmark;
import net.emilla.command.core.Calculate;
import net.emilla.command.core.Calendar;
import net.emilla.command.core.Call;
import net.emilla.command.core.Contact;
import net.emilla.command.core.Copy;
import net.emilla.command.core.CoreCommand;
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
import net.emilla.util.Features;

public class SettingVals {

    public static DefaultCommandWrapper.Yielder defaultCommand(SharedPreferences prefs,
            CoreCommand.Yielder[] coreYielders) {
        // Todo: allow apps and customs. Make sure to fall back to a core if the app is uninstalled
        //  or the custom is deleted.
        String entry = prefs.getString("default_command", "web");
        return new DefaultCommandWrapper.Yielder(switch (entry) {
            case Call.ENTRY -> coreYielders[0];
            case Dial.ENTRY -> coreYielders[1];
            case Sms.ENTRY -> coreYielders[2];
            case Email.ENTRY -> coreYielders[3];
            case Navigate.ENTRY -> coreYielders[4];
            case Launch.ENTRY -> coreYielders[5];
            case Copy.ENTRY -> coreYielders[6];
            case Share.ENTRY -> coreYielders[7];
            case Settings.ENTRY -> coreYielders[8];
//            case Note.ENTRY -> coreYielders[];
//            case Todo.ENTRY -> coreYielders[];
            case Web.ENTRY -> coreYielders[9];
//            case Find.ENTRY -> coreYielders[];
            case Time.ENTRY -> coreYielders[10];
            case Alarm.ENTRY -> coreYielders[11];
            case Timer.ENTRY -> coreYielders[12];
            case Pomodoro.ENTRY -> coreYielders[13];
            case Calendar.ENTRY -> coreYielders[14];
            case Contact.ENTRY -> coreYielders[15];
//            case Notify.ENTRY -> coreYielders[];
            case Calculate.ENTRY -> coreYielders[16];
            case Weather.ENTRY -> coreYielders[17];
            case Bookmark.ENTRY -> coreYielders[18];
            case Torch.ENTRY -> coreYielders[19];
            case Info.ENTRY -> coreYielders[20];
            case Uninstall.ENTRY -> coreYielders[21];
            case Toast.ENTRY -> coreYielders[22];
            default -> throw new IllegalArgumentException("No such command \"" + entry + "\".");
        });
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

    public static boolean showHelpButton(SharedPreferences prefs) {
        return prefs.getBoolean("show_help_button", true);
        // Todo: put these in an editor.
    }

    public static boolean showCursorStartButton(SharedPreferences prefs) {
        return prefs.getBoolean("show_cursor_start_button", false);
        // Todo: put these in an editor.
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

    public static QuickAction menuKey(SharedPreferences prefs, AssistActivity act) {
        return quickAction(prefs, QuickAction.PREF_MENU_KEY, QuickAction.HELP, act);
    }

    private static QuickAction quickAction(SharedPreferences prefs, String actionPref,
            String defaultAction, AssistActivity act) {
        return switch (prefs.getString(actionPref, defaultAction)) {
            case QuickAction.FLASHLIGHT -> new Flashlight(act);
            case QuickAction.ASSISTANT_SETTINGS -> new AssistantSettings(act);
            case QuickAction.SELECT_ALL -> new SelectAll(act);
            case QuickAction.HELP -> new Help(act);
            default -> new NoAction(act);
        };
    }

    public static Chimer chimer(Context ctx, SharedPreferences prefs) {
        return switch (soundSet(prefs)) {
            case Chimer.NONE -> new Silence();
            case Chimer.NEBULA -> new Nebula(ctx);
            case Chimer.VOICE_DIALER -> new Redial();
            case Chimer.CUSTOM -> new Custom(ctx, prefs);
            default -> throw new RuntimeException("Not a chimer.");
        };
    }

    public static String soundSet(SharedPreferences prefs) {
        return prefs.getString(Chimer.SOUND_SET, Chimer.NEBULA);
    }

    public static String bookmarkCsv(SharedPreferences prefs) {
        return prefs.getString("medias", Bookmark.DFLT_BOOKMARKS);
    }

    public static String searchEngineCsv(SharedPreferences prefs) {
        return prefs.getString("search_engines", Web.DFLT_SEARCH_ENGINES);
    }
}
