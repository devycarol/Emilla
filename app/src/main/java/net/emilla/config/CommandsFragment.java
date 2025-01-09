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
import net.emilla.command.app.AospContacts;
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
import net.emilla.settings.Aliases;

import java.util.Set;

public class CommandsFragment extends PreferenceFragmentCompat {
private void setupPref(EditTextPreference cmdPref, String setKey,
        OnPreferenceChangeListener listener, SharedPreferences prefs,
        Resources res, @ArrayRes int setId) {
    Set<String> aliases = prefs.getStringSet(setKey,  Set.of(res.getStringArray(setId)));
    cmdPref.setText(String.join(", ", aliases));
    cmdPref.setOnPreferenceChangeListener(listener);
}

private void setupAppPref(String pkg, SharedPreferences prefs, Resources res,
        PackageManager pm, OnPreferenceChangeListener listener) {
    EditTextPreference appCmdPref = findPreference("aliases_" + pkg + "_text");
    if (appCmdPref != null) try {
        ApplicationInfo info = pm.getApplicationInfo(pkg, 0);
        CharSequence label = pm.getApplicationLabel(info);
        appCmdPref.setTitle(label);
        // this uses the application icon and doesn't account for multiple launcher icons yet
        Drawable appIcon = pm.getApplicationIcon(pkg);
        appCmdPref.setIcon(appIcon);
        setupPref(appCmdPref, "aliases_" + pkg, listener, prefs, res, Aliases.appSetId(pkg, Markor.CLS_MAIN /*Todo: procedurally generate these prefs*/));
    } catch (PackageManager.NameNotFoundException e) {
        appCmdPref.setVisible(false);
    }
}

private void setupApps(EmillaActivity act, SharedPreferences prefs, Resources res,
        OnPreferenceChangeListener listener) {
    PackageManager pm = act.getPackageManager();
    setupAppPref(AospContacts.PKG, prefs, res, pm, listener);
    setupAppPref(Markor.PKG, prefs, res, pm, listener);
    setupAppPref(Firefox.PKG, prefs, res, pm, listener);
    setupAppPref(Tor.PKG, prefs, res, pm, listener);
    setupAppPref(Signal.PKG, prefs, res, pm, listener);
    setupAppPref(Newpipe.PKG, prefs, res, pm, listener);
    setupAppPref(Tubular.PKG, prefs, res, pm, listener);
    setupAppPref(Github.PKG, prefs, res, pm, listener);
    setupAppPref(Youtube.PKG, prefs, res, pm, listener);
    setupAppPref(Discord.PKG, prefs, res, pm, listener);
    setupAppPref(Outlook.PKG, prefs, res, pm, listener);
    // Todo: procedurally generate these
}

private void setupCorePref(String textKey, OnPreferenceChangeListener listener,
        SharedPreferences prefs, Resources res, @ArrayRes int setId) {
    EditTextPreference cmdPref = findPreference(textKey);
    if (cmdPref == null) return;
    String setKey = textKey.substring(0, textKey.length() - 5);
    setupPref(cmdPref, setKey, listener, prefs, res, setId);
}

private void deactivate(String textKey, OnPreferenceClickListener listener) {
    Preference cmdPref = findPreference(textKey);
    if (cmdPref != null) cmdPref.setOnPreferenceClickListener(listener);
}

private void setupCores(EmillaActivity act, SharedPreferences prefs, Resources res,
        OnPreferenceChangeListener listener) {
    if (prefs == null) return;
    OnPreferenceClickListener dListener = pref -> {
        act.toast("Coming soon!");
        return false;
    };
    setupCorePref("aliases_call_text", listener, prefs, res, R.array.aliases_call);
    setupCorePref("aliases_dial_text", listener, prefs, res, R.array.aliases_dial);
    setupCorePref("aliases_sms_text", listener, prefs, res, R.array.aliases_sms);
    setupCorePref("aliases_email_text", listener, prefs, res, R.array.aliases_email);
    setupCorePref("aliases_copy_text", listener, prefs, res, R.array.aliases_copy);
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
    setupCorePref("aliases_bookmark_text", listener, prefs, res, R.array.aliases_bookmark);
    setupCorePref("aliases_torch_text", listener, prefs, res, R.array.aliases_torch);
    setupCorePref("aliases_info_text", listener, prefs, res, R.array.aliases_info);
    setupCorePref("aliases_toast_text", listener, prefs, res, R.array.aliases_toast);
    deactivate("aliases_custom_text", dListener);
}

@Override
public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.command_prefs, rootKey);
    EmillaActivity act = (EmillaActivity) requireActivity();
    SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
    Resources res = getResources();
    OnPreferenceChangeListener listener = (pref, newVal) -> {
        String textKey = pref.getKey();
        String setKey = textKey.substring(0, textKey.length() - 5);
        String correctedText = ((String) newVal).trim().toLowerCase();
        String[] vals = (correctedText.split(" *, *"));
        Set<String> aliases = Set.of(vals);
        String joined = String.join(", ", aliases);
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