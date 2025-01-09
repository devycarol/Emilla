package net.emilla.settings;

import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.command.app.AospContacts;
import net.emilla.command.app.AppCommand.AppInfo;
import net.emilla.command.app.Discord;
import net.emilla.command.app.Firefox;
import net.emilla.command.app.Github;
import net.emilla.command.app.Markor;
import net.emilla.command.app.Newpipe;
import net.emilla.command.app.Outlook;
import net.emilla.command.app.Signal;
import net.emilla.command.app.Tor;
import net.emilla.command.app.Tubular;
import net.emilla.command.app.Youtube;

import java.util.Set;

public class Aliases {

    private static final int[] SETS = {
        R.array.aliases_call,
        R.array.aliases_dial,
        R.array.aliases_sms,
        R.array.aliases_email,
        R.array.aliases_navigate,
        R.array.aliases_launch,
        R.array.aliases_copy,
        R.array.aliases_share,
        R.array.aliases_settings,
    //    R.array.aliases_note,
    //    R.array.aliases_todo,
        R.array.aliases_web,
    //    R.array.aliases_find,
        R.array.aliases_clock,
        R.array.aliases_alarm,
        R.array.aliases_timer,
        R.array.aliases_pomodoro,
        R.array.aliases_calendar,
        R.array.aliases_contact,
    //    R.array.aliases_notify,
        R.array.aliases_calculate,
        R.array.aliases_weather,
        R.array.aliases_bookmark,
        R.array.aliases_torch,
        R.array.aliases_info,
        R.array.aliases_uninstall,
        R.array.aliases_toast
    };

    public static final String[] PREFS = {
        "aliases_call",
        "aliases_dial",
        "aliases_sms",
        "aliases_email",
        "aliases_navigate",
        "aliases_launch",
        "aliases_copy",
        "aliases_share",
        "aliases_settings",
    //    "aliases_note",
    //    "aliases_todo",
        "aliases_web",
    //    "aliases_find",
        "aliases_clock",
        "aliases_alarm",
        "aliases_timer",
        "aliases_pomodoro",
        "aliases_calendar",
        "aliases_contact",
    //    "aliases_notify",
        "aliases_calculate",
        "aliases_weather",
        "aliases_bookmark",
        "aliases_torch",
        "aliases_info",
        "aliases_uninstall",
        "aliases_toast"
    };

    public static Set<String> set(SharedPreferences prefs, Resources res, int idx) {
        return prefs.getStringSet(PREFS[idx], Set.of(res.getStringArray(SETS[idx])));
    }

    @ArrayRes
    public static int appSetId(String pkg, String cls) {
        return switch (pkg) {
        case AospContacts.PKG -> R.array.aliases_aosp_contacts;
        case Markor.PKG -> cls.equals(Markor.CLS_MAIN) ? R.array.aliases_markor : 0;
        // Markor can have multiple launchers, only the main one should have the aliases.
        case Firefox.PKG -> R.array.aliases_firefox;
        case Tor.PKG -> R.array.aliases_tor;
        case Signal.PKG -> R.array.aliases_signal;
        case Newpipe.PKG -> R.array.aliases_newpipe;
        case Tubular.PKG -> R.array.aliases_tubular;
        case Github.PKG -> R.array.aliases_github;
        case Youtube.PKG -> R.array.aliases_youtube;
        case Discord.PKG -> R.array.aliases_discord;
        case Outlook.PKG -> R.array.aliases_outlook;
        default -> 0;
        };
    }

    @Nullable
    public static Set<String> appSet(SharedPreferences prefs, Resources res, AppInfo info) {
        @ArrayRes int setId = appSetId(info.pkg, info.cls);
        if (setId != 0) {
            return prefs.getStringSet("aliases_" + info.pkg, Set.of(res.getStringArray(setId)));
        }
        return null;
    }
}
