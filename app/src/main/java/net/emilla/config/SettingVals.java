package net.emilla.config;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;

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
import net.emilla.apps.Apps;
import net.emilla.chime.Chime;
import net.emilla.chime.Chimer;
import net.emilla.command.DefaultCommandWrapperYielder;
import net.emilla.command.core.CoreEntry;
import net.emilla.util.Features;

import java.util.HashSet;
import java.util.Set;

public final class SettingVals {

    public static final String CHIMER = "sound_set";

    public static final String ALIASES_CUSTOM = "aliases_custom";
    public static final String ALIASES_CUSTOM_TEXT = "aliases_custom_text";

    private static final String DEFAULT_SEARCH_ENGINES = """
        Wikipedia, wiki, w, https://wikipedia.org/wiki/%s
        Google, g, https://www.google.com/search?q=%s
        Google Images, gimages, gimage, gimg, gi, https://www.google.com/search?q=%s&udm=2
        YouTube, yt, y, https://www.youtube.com/results?search_query=%s
        DuckDuckGo, ddg, dd, d, https://duckduckgo.com/?q=%s
        DuckDuckGo Images, duckimages, duckimage, duckimg, ddgimages, ddgimage, ddgimg, ddgi, ddimages, ddimage, ddimg, ddi, dimages, dimage, dimg, https://duckduckgo.com/?q=%s&ia=images&iax=images""";

    public static boolean commandEnabled(
        PackageManager pm,
        SharedPreferences prefs,
        CoreEntry coreEntry
    ) {
        String key = commandEnabledKey(coreEntry.entry);
        return prefs.getBoolean(key, prefs.contains(key) || coreEntry.isPossible(pm));
        // don't check 'possible' if a key is already registered. note this demands exhaustively
        // ensuring command possibility in the *settings screens*, re-checking device properties to
        // ensure commands haven't been rendered impossible as a result of app or device changes.
        // examples include apps being installed/removed or moving devices via data backup. this
        // implies edge cases where commands have become impossible since the last settings visit!
        // be sure to still write exception-safe code.
    }

    public static boolean appEnabled(SharedPreferences prefs, String pkg, String cls) {
        String key = appEnabledKey(pkg, cls);
        return prefs.getBoolean(key, true /*allowProprietary(prefs) || isFoss(pkg)*/);
    }

    public static String appEnabledKey(String pkg, String cls) {
        return commandEnabledKey(Apps.entry(pkg, cls));
    }

    public static String commandEnabledKey(String entry) {
        return "cmd_" + entry + "_enabled";
    }

    public static DefaultCommandWrapperYielder defaultCommand(SharedPreferences prefs) {
        // Todo: allow apps and customs. Make sure to fall back to a core if the app is uninstalled
        //  or the custom is deleted.
        String entry = prefs.getString("default_command", "web");
        return new DefaultCommandWrapperYielder(CoreEntry.of(entry));
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
                DisplayMetrics metrics = res.getDisplayMetrics();
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

    public static String chimerId(SharedPreferences prefs) {
        return prefs.getString(CHIMER, Chimer.NEBULA);
    }

    public static void setCustomSound(SharedPreferences prefs, Chime chime, Uri soundUri) {
        prefs.edit().putString(chime.preferenceKey, soundUri.toString()).apply();
    }

    @Nullable
    public static Uri customChimeSoundUri(SharedPreferences prefs, Chime chime) {
        String uriString = prefs.getString(chime.preferenceKey, null);
        if (uriString != null) {
            return Uri.parse(uriString);
        }
        return null;
    }

    public static void deleteCustomChimeSound(SharedPreferences prefs, Chime chime) {
        prefs.edit().remove(chime.preferenceKey).apply();
    }

    public static String searchEngineCsv(SharedPreferences prefs) {
        return prefs.getString("search_engines", DEFAULT_SEARCH_ENGINES);
    }

    public static Set<String> snippets(SharedPreferences prefs) {
        return prefs.getStringSet("snippets", defaultSnippets());
    }

    public static String snippet(SharedPreferences prefs, String label) {
        return prefs.getString(snippetPref(label), "");
    }

    public static void addSnippet(SharedPreferences prefs, String label, String text) {
        var snippets = new HashSet<String>(prefs.getStringSet("snippets", defaultSnippets()));
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
        var snippets = new HashSet<String>(prefs.getStringSet("snippets", defaultSnippets()));
        snippets.remove(label);

        prefs.edit().remove(snippetPref(label))
                    .putStringSet("snippets", snippets)
                    .apply();
    }

    private static Set<String> defaultSnippets() {
        return Set.of();
    }

    private static String snippetPref(String label) {
        return "snippet_" + label;
    }

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

    private SettingVals() {}
}
