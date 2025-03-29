package net.emilla.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import net.emilla.R;
import net.emilla.action.AssistantSettings;
import net.emilla.action.CursorStart;
import net.emilla.action.Flashlight;
import net.emilla.action.Help;
import net.emilla.action.NoAction;
import net.emilla.action.PlayPause;
import net.emilla.action.QuickAction;
import net.emilla.action.SelectAll;
import net.emilla.activity.AssistActivity;
import net.emilla.chime.Chimer;
import net.emilla.chime.Custom;
import net.emilla.chime.Nebula;
import net.emilla.chime.Redial;
import net.emilla.chime.Silence;
import net.emilla.command.DefaultCommandWrapper;
import net.emilla.command.core.Alarm;
import net.emilla.command.core.Bits;
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
import net.emilla.command.core.Notify;
import net.emilla.command.core.Pause;
import net.emilla.command.core.Play;
import net.emilla.command.core.Pomodoro;
import net.emilla.command.core.RandomNumber;
import net.emilla.command.core.Roll;
import net.emilla.command.core.Share;
import net.emilla.command.core.Sms;
import net.emilla.command.core.Snippets;
import net.emilla.command.core.Time;
import net.emilla.command.core.Timer;
import net.emilla.command.core.Toast;
import net.emilla.command.core.Torch;
import net.emilla.command.core.Uninstall;
import net.emilla.command.core.Weather;
import net.emilla.command.core.Web;
import net.emilla.util.Features;

import java.util.HashSet;
import java.util.Set;

public final class SettingVals {

    public static final String
            ALIASES_CUSTOM = "aliases_custom",
            ALIASES_CUSTOM_TEXT = "aliases_custom_text";

    public static boolean commandEnabled(PackageManager pm, SharedPreferences prefs, String entry) {
        String key = commandEnabledKey(entry);
        return prefs.getBoolean(key, prefs.contains(key) || CoreCommand.possible(pm, entry));
        // don't check 'possible' if a key is already registered. note this demands exhaustively
        // ensuring command possibility in the *settings screens*, re-checking device properties to
        // ensure commands haven't been rendered impossible as a result of app or device changes.
        // examples include apps being installed/removed or moving devices via data backup. this
        // implies edge cases where commands have become impossible since the last settings visit!
        // be sure to still write exception-safe code.
    }

    public static boolean appEnabled(SharedPreferences prefs, String pkg) {
        String key = commandEnabledKey(pkg);
        return prefs.getBoolean(key, true /*allowProprietary(prefs) || isFoss(pkg)*/);
    }

    public static String commandEnabledKey(String entry) {
        return "cmd_" + entry + "_enabled";
    }

    public static DefaultCommandWrapper.Yielder defaultCommand(
        SharedPreferences prefs,
        CoreCommand.Yielder[] coreYielders
    ) {
        // Todo: allow apps and customs. Make sure to fall back to a core if the app is uninstalled
        //  or the custom is deleted.
        var entry = prefs.getString("default_command", "web");
        CoreCommand.Yielder yielder = coreYielders[yielderIndex(entry)];
        return new DefaultCommandWrapper.Yielder(yielder);
    }

    private static int yielderIndex(String cmdEntry) {
        return switch (cmdEntry) {
            case Call.ENTRY         ->  0;
            case Dial.ENTRY         ->  1;
            case Sms.ENTRY          ->  2;
            case Email.ENTRY        ->  3;
            case Navigate.ENTRY     ->  4;
            case Launch.ENTRY       ->  5;
            case Copy.ENTRY         ->  6;
            case Snippets.ENTRY     ->  7;
            case Share.ENTRY        ->  8;
//            case Setting.ENTRY      ->   ;
//            case Note.ENTRY         ->   ;
//            case Todo.ENTRY         ->   ;
            case Web.ENTRY          ->  9;
//            case Find.ENTRY         ->   ;
            case Time.ENTRY         -> 10;
            case Alarm.ENTRY        -> 11;
            case Timer.ENTRY        -> 12;
            case Pomodoro.ENTRY     -> 13;
            case Calendar.ENTRY     -> 14;
            case Contact.ENTRY      -> 15;
            case Notify.ENTRY       -> 16;
            case Calculate.ENTRY    -> 17;
            case RandomNumber.ENTRY -> 18;
            case Roll.ENTRY         -> 19;
            case Bits.ENTRY         -> 20;
            case Weather.ENTRY      -> 21;
            case Play.ENTRY         -> 22;
            case Pause.ENTRY        -> 23;
            case Torch.ENTRY        -> 24;
            case Info.ENTRY         -> 25;
            case Uninstall.ENTRY    -> 26;
            case Toast.ENTRY        -> 27;
            default -> throw new IllegalArgumentException("No such command \"" + cmdEntry + "\".");
        };
    }

    public static Set<String> customCommands(SharedPreferences prefs) {
        return prefs.getStringSet(ALIASES_CUSTOM, Set.of());
    }

    public static boolean showTitlebar(SharedPreferences prefs, Resources res) {
        return switch (prefs.getString("show_titlebar", res.getString(R.string.conf_show_titlebar))) {
            // Todo: in the off chance app-data is transferred across phone/tablet, the setting
            //  should change if it's still default. back:prefs.xml
            case "never" -> false;
            case "portrait" -> {
                var metrics = res.getDisplayMetrics();
                yield metrics.widthPixels < metrics.heightPixels;
            }
            default /*"always"*/ -> true;
        };
    }

    public static String motd(SharedPreferences prefs, Resources res) {
        return prefs.getString("motd", res.getString(R.string.activity_assistant));
    }

    public static boolean alwaysShowData(SharedPreferences prefs) {
        return prefs.getBoolean("always_show_data", false);
        // TODO ACC: no reason for a hidden data field if a screen reader is in use.
    }

    public static boolean showHelpButton(SharedPreferences prefs) {
        return prefs.getBoolean("show_help_button", true);
        // Todo: put these in an editor.
    }

    public static boolean showCursorStartButton(SharedPreferences prefs) {
        return prefs.getBoolean("show_cursor_start_button", false);
        // Todo: put these in an editor.
    }

    public static boolean showPlayPauseButton(SharedPreferences prefs) {
        return prefs.getBoolean("show_play_pause_button", false);
        // Todo: put these in an editor.
    }

    public static QuickAction noCommand(SharedPreferences prefs, AssistActivity act) {
        return quickAction(prefs, QuickAction.PREF_NO_COMMAND, QuickAction.ASSISTANT_SETTINGS, act);
    }

    public static QuickAction longSubmit(SharedPreferences prefs, AssistActivity act) {
        return quickAction(prefs, QuickAction.PREF_LONG_SUBMIT, QuickAction.SELECT_ALL, act);
    }

    public static QuickAction doubleAssist(
        SharedPreferences prefs,
        AssistActivity act,
        PackageManager pm
    ) {
        String defaultAction = Features.torch(pm) ? QuickAction.FLASHLIGHT
                : QuickAction.ASSISTANT_SETTINGS;
        return quickAction(prefs, QuickAction.PREF_DOUBLE_ASSIST, defaultAction, act);
    }

    public static QuickAction menuKey(SharedPreferences prefs, AssistActivity act) {
        return quickAction(prefs, QuickAction.PREF_MENU_KEY, QuickAction.HELP, act);
    }

    private static QuickAction quickAction(
        SharedPreferences prefs,
        String actionPref,
        String defaultAction,
        AssistActivity act
    ) {
        return switch (prefs.getString(actionPref, defaultAction)) {
            case QuickAction.FLASHLIGHT -> new Flashlight(act);
            case QuickAction.ASSISTANT_SETTINGS -> new AssistantSettings(act);
            case QuickAction.SELECT_ALL -> new SelectAll(act);
            case QuickAction.CURSOR_START -> new CursorStart(act);
            case QuickAction.PLAY_PAUSE -> new PlayPause(act);
            case QuickAction.HELP -> new Help(act);
            default -> new NoAction(act);
        };
    }

    /**
     * The user's preferred chimer for audio feedback.
     *
     * @param appCtx it's important to use the application context to avoid memory leaks!
     * @param prefs used to build the chimer from user settings.
     * @return the user's chosen chimer.
     */
    public static Chimer chimer(Context appCtx, SharedPreferences prefs) {
        return switch (soundSet(prefs)) {
            case Chimer.NONE -> new Silence();
            case Chimer.NEBULA -> new Nebula(appCtx);
            case Chimer.VOICE_DIALER -> new Redial();
            case Chimer.CUSTOM -> new Custom(appCtx, prefs);
            default -> throw new IllegalArgumentException();
        };
    }

    public static String soundSet(SharedPreferences prefs) {
        return prefs.getString(Chimer.SOUND_SET, Chimer.NEBULA);
    }

    public static String searchEngineCsv(SharedPreferences prefs) {
        return prefs.getString("search_engines", Web.DFLT_SEARCH_ENGINES);
    }

    public static Set<String> snippets(SharedPreferences prefs) {
        return prefs.getStringSet("snippets", Snippets.DFLT_SNIPPETS);
    }

    public static String snippet(SharedPreferences prefs, String label) {
        return prefs.getString(snippetPref(label), "");
    }

    public static void addSnippet(SharedPreferences prefs, String label, String text) {
        Set<String> snippets = new HashSet<>(prefs.getStringSet("snippets", Snippets.DFLT_SNIPPETS));
        snippets.add(label);

        prefs.edit().putString(snippetPref(label), text)
                    .putStringSet("snippets", snippets)
                    .apply();
    }

    public static void replaceSnippet(SharedPreferences prefs, String label, String text) {
        prefs.edit().putString(snippetPref(label), text)
                    .apply();
    }

    public static void removeSnippet(SharedPreferences prefs, String label) {
        Set<String> snippets = new HashSet<>(prefs.getStringSet("snippets", Snippets.DFLT_SNIPPETS));
        snippets.remove(label);

        prefs.edit().remove(snippetPref(label))
                    .putStringSet("snippets", snippets)
                    .apply();
    }

    private static String snippetPref(String label) {
        return "snippet_" + label;
    }

    private SettingVals() {}

    public static int defaultPomoWorkMins(SharedPreferences prefs) {
        return prefs.getInt("pomo_default_work_mins", 25);
        // Todo config.
    }

    public static int defaultPomoBreakMins(SharedPreferences prefs) {
        return prefs.getInt("pomo_default_break_mins", 5);
        // Todo config.
    }

    public static String defaultPomoWorkMemo(SharedPreferences prefs, Resources res) {
        return prefs.getString("pomo_default_work_memo", res.getString(R.string.ping_pomodoro_text));
    }

    public static String defaultPomoBreakMemo(SharedPreferences prefs, Resources res) {
        return prefs.getString("pomo_default_break_memo", res.getString(R.string.ping_pomodoro_break_text));
    }
}
