package net.emilla.settings;

import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.commands.AppCmdInfo;
import net.emilla.utils.Apps;

import java.util.Set;

public class Aliases {
private static final int[] SETS = {
    R.array.aliases_call,
    R.array.aliases_dial,
    R.array.aliases_sms,
    R.array.aliases_email,
    R.array.aliases_navigate,
    R.array.aliases_launch,
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
    R.array.aliases_view,
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
    "aliases_view",
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
    case Apps.PKG_AOSP_CONTACTS -> R.array.aliases_aosp_contacts;
    case Apps.PKG_MARKOR -> cls.equals(AppCmdInfo.CLS_MARKOR_MAIN) ? R.array.aliases_markor : -1;
    // Markor can have multiple launchers. Only the main one should have the aliases.
    case Apps.PKG_FIREFOX -> R.array.aliases_firefox;
    case Apps.PKG_TOR -> R.array.aliases_tor;
    case Apps.PKG_SIGNAL -> R.array.aliases_signal;
    case Apps.PKG_NEWPIPE -> R.array.aliases_newpipe;
    case Apps.PKG_TUBULAR -> R.array.aliases_tubular;
    case Apps.PKG_GITHUB -> R.array.aliases_github;
    case Apps.PKG_YOUTUBE -> R.array.aliases_youtube;
    case Apps.PKG_DISCORD -> R.array.aliases_discord;
    default -> -1;
    };
}

@Nullable
public static Set<String> appSet(SharedPreferences prefs, Resources res,
        String pkg, String cls) {
    @ArrayRes int setId = appSetId(pkg, cls);
    return setId == -1 ? null : prefs.getStringSet("aliases_" + pkg, Set.of(res.getStringArray(setId)));
}
}
