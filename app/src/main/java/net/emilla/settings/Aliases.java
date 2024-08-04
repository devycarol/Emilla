package net.emilla.settings;

import android.content.SharedPreferences;
import android.content.res.Resources;

import net.emilla.R;

import java.util.Set;

public class Aliases {
private static final int[] SETS = {
    R.array.aliases_call,
    R.array.aliases_dial,
    R.array.aliases_sms,
    R.array.aliases_email,
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
    R.array.aliases_toast
};

public static final String[] PREFS = {
    "aliases_call",
    "aliases_dial",
    "aliases_sms",
    "aliases_email",
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
    "aliases_toast"
};

public static Set<String> set(final SharedPreferences prefs, final Resources res, final int idx) {
    return prefs.getStringSet(PREFS[idx], Set.of(res.getStringArray(SETS[idx])));
}
}
