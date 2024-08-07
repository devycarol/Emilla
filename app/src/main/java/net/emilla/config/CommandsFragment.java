package net.emilla.config;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.ArrayRes;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragmentCompat;

import net.emilla.EmillaActivity;
import net.emilla.R;
import net.emilla.commands.AppCmdInfo;
import net.emilla.settings.Aliases;
import net.emilla.utils.Apps;

import java.util.Set;

public class CommandsFragment extends PreferenceFragmentCompat {
private void setupPref(final EditTextPreference cmdPref, final String setKey,
        final OnPreferenceChangeListener listener, final SharedPreferences prefs,
        final Resources res, @ArrayRes final int setId) {
    final Set<String> aliases = prefs.getStringSet(setKey,  Set.of(res.getStringArray(setId)));
    cmdPref.setText(String.join(", ", aliases));
    cmdPref.setOnPreferenceChangeListener(listener);
}

private void setupAppPref(final String pkg, final SharedPreferences prefs, final Resources res,
        final PackageManager pm, final OnPreferenceChangeListener listener) {
    final EditTextPreference appCmdPref = findPreference("aliases_" + pkg + "_text");
    if (appCmdPref != null) try {
        final ApplicationInfo info = pm.getApplicationInfo(pkg, 0);
        final CharSequence label = pm.getApplicationLabel(info);
        appCmdPref.setTitle(label);
        // this uses the application icon and doesn't account for multiple launcher icons yet
        final Drawable appIcon = pm.getApplicationIcon(pkg);
        appCmdPref.setIcon(appIcon);
        setupPref(appCmdPref, "aliases_" + pkg, listener, prefs, res, Aliases.appSetId(pkg, AppCmdInfo.CLS_MARKOR_MAIN /*Todo: procedurally generate these prefs*/));
    } catch (PackageManager.NameNotFoundException e) {
        appCmdPref.setVisible(false);
    }
}

private void setupApps(final EmillaActivity act, final SharedPreferences prefs, final Resources res,
        final OnPreferenceChangeListener listener) {
    final PackageManager pm = act.getPackageManager();
    setupAppPref(Apps.PKG_AOSP_CONTACTS, prefs, res, pm, listener);
    setupAppPref(Apps.PKG_MARKOR, prefs, res, pm, listener);
    setupAppPref(Apps.PKG_FIREFOX, prefs, res, pm, listener);
    setupAppPref(Apps.PKG_TOR, prefs, res, pm, listener);
    setupAppPref(Apps.PKG_SIGNAL, prefs, res, pm, listener);
    setupAppPref(Apps.PKG_NEWPIPE, prefs, res, pm, listener);
    setupAppPref(Apps.PKG_TUBULAR, prefs, res, pm, listener);
    setupAppPref(Apps.PKG_GITHUB, prefs, res, pm, listener);
    setupAppPref(Apps.PKG_YOUTUBE, prefs, res, pm, listener);
    setupAppPref(Apps.PKG_DISCORD, prefs, res, pm, listener);
    // Todo: procedurally generate these
}

private void setupCorePref(final String textKey, final OnPreferenceChangeListener listener,
        final SharedPreferences prefs, final Resources res, @ArrayRes final int setId) {
    final EditTextPreference cmdPref = findPreference(textKey);
    if (cmdPref == null) return;
    final String setKey = textKey.substring(0, textKey.length() - 5);
    setupPref(cmdPref, setKey, listener, prefs, res, setId);
}

private void deactivate(final String textKey, final OnPreferenceClickListener listener) {
    final Preference cmdPref = findPreference(textKey);
    if (cmdPref != null) cmdPref.setOnPreferenceClickListener(listener);
}

private void setupCores(final EmillaActivity act, final SharedPreferences prefs, final Resources res,
        final OnPreferenceChangeListener listener) {
    if (prefs == null) return;
    final OnPreferenceClickListener dListener = pref -> {
        act.toast("Coming soon!", false);
        return false;
    };
    setupCorePref("aliases_call_text", listener, prefs, res, R.array.aliases_call);
    setupCorePref("aliases_dial_text", listener, prefs, res, R.array.aliases_dial);
    setupCorePref("aliases_sms_text", listener, prefs, res, R.array.aliases_sms);
    setupCorePref("aliases_email_text", listener, prefs, res, R.array.aliases_email);
    setupCorePref("aliases_share_text", listener, prefs, res, R.array.aliases_share);
    setupCorePref("aliases_launch_text", listener, prefs, res, R.array.aliases_launch);
    setupCorePref("aliases_settings_text", listener, prefs, res, R.array.aliases_settings);
    deactivate("aliases_note_text", dListener);
    deactivate("aliases_todo_text", dListener);
    setupCorePref("aliases_web_text", listener, prefs, res, R.array.aliases_web);
    deactivate("aliases_find_text", dListener);
    setupCorePref("aliases_clock_text", listener, prefs, res, R.array.aliases_clock);
    setupCorePref("aliases_alarm_text", listener, prefs, res, R.array.aliases_alarm);
    setupCorePref("aliases_timer_text", listener, prefs, res, R.array.aliases_timer);
    setupCorePref("aliases_pomodoro_text", listener, prefs, res, R.array.aliases_pomodoro);
    setupCorePref("aliases_calendar_text", listener, prefs, res, R.array.aliases_calendar);
    setupCorePref("aliases_contact_text", listener, prefs, res, R.array.aliases_contact);
    deactivate("aliases_notify_text", dListener);
    setupCorePref("aliases_calculate_text", listener, prefs, res, R.array.aliases_calculate);
    setupCorePref("aliases_weather_text", listener, prefs, res, R.array.aliases_weather);
    setupCorePref("aliases_view_text", listener, prefs, res, R.array.aliases_view);
    setupCorePref("aliases_info_text", listener, prefs, res, R.array.aliases_info);
    setupCorePref("aliases_toast_text", listener, prefs, res, R.array.aliases_toast);
    deactivate("aliases_custom_text", dListener);
}

@Override
public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
    setPreferencesFromResource(R.xml.command_prefs, rootKey);
    final EmillaActivity act = (EmillaActivity) requireActivity();
    final SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
    final Resources res = getResources();
    final OnPreferenceChangeListener listener = (pref, newVal) -> {
        final String textKey = pref.getKey();
        final String setKey = textKey.substring(0, textKey.length() - 5);
        final String correctedText = ((String) newVal).trim().toLowerCase();
        final String[] vals = (correctedText.split(" *, *"));
        final Set<String> aliases = Set.of(vals);
        final String joined = String.join(", ", aliases);
        ((EditTextPreference) pref).setText(joined);
        prefs.edit()
                .putString(textKey, joined)
                .putStringSet(setKey, aliases).apply();
        return false;
    };
    setupCores(act, prefs, res, listener);
    setupApps(act, prefs, res, listener);
}
}