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
import net.emilla.command.core.Alarm;
import net.emilla.command.core.Bookmark;
import net.emilla.command.core.Calculate;
import net.emilla.command.core.Calendar;
import net.emilla.command.core.Call;
import net.emilla.command.core.Contact;
import net.emilla.command.core.Copy;
import net.emilla.command.core.Dial;
import net.emilla.command.core.Email;
import net.emilla.command.core.Find;
import net.emilla.command.core.Info;
import net.emilla.command.core.Launch;
import net.emilla.command.core.Note;
import net.emilla.command.core.Notify;
import net.emilla.command.core.Pomodoro;
import net.emilla.command.core.Settings;
import net.emilla.command.core.Share;
import net.emilla.command.core.Sms;
import net.emilla.command.core.Time;
import net.emilla.command.core.Timer;
import net.emilla.command.core.Toast;
import net.emilla.command.core.Todo;
import net.emilla.command.core.Torch;
import net.emilla.command.core.Weather;
import net.emilla.command.core.Web;
import net.emilla.settings.Aliases;

import java.util.Set;

public class CommandsFragment extends PreferenceFragmentCompat {

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

    private void setupCores(EmillaActivity act, SharedPreferences prefs, Resources res,
            OnPreferenceChangeListener listener) {
        if (prefs == null) return;
        setupCorePref(Call.ALIAS_TEXT_KEY, listener, prefs, res, Call.ALIASES);
        setupCorePref(Dial.ALIAS_TEXT_KEY, listener, prefs, res, Dial.ALIASES);
        setupCorePref(Sms.ALIAS_TEXT_KEY, listener, prefs, res,  Sms.ALIASES);
        setupCorePref(Email.ALIAS_TEXT_KEY, listener, prefs, res, Email.ALIASES);
        setupCorePref(Copy.ALIAS_TEXT_KEY, listener, prefs, res, Copy.ALIASES);
        setupCorePref(Share.ALIAS_TEXT_KEY, listener, prefs, res, Share.ALIASES);
        setupCorePref(Launch.ALIAS_TEXT_KEY, listener, prefs, res, Launch.ALIASES);
        setupCorePref(Settings.ALIAS_TEXT_KEY, listener, prefs, res,  Settings.ALIASES);
        deactivate(Note.ALIAS_TEXT_KEY, act);
        deactivate(Todo.ALIAS_TEXT_KEY, act);
        setupCorePref(Web.ALIAS_TEXT_KEY, listener, prefs, res, Web.ALIASES);
        deactivate(Find.ALIAS_TEXT_KEY, act);
        setupCorePref(Time.ALIAS_TEXT_KEY, listener, prefs, res, Time.ALIASES);
        setupCorePref(Alarm.ALIAS_TEXT_KEY, listener, prefs, res, Alarm.ALIASES);
        setupCorePref(Timer.ALIAS_TEXT_KEY, listener, prefs, res, Timer.ALIASES);
        setupCorePref(Pomodoro.ALIAS_TEXT_KEY, listener, prefs, res, Pomodoro.ALIASES);
        setupCorePref(Calendar.ALIAS_TEXT_KEY, listener, prefs, res, Calendar.ALIASES);
        setupCorePref(Contact.ALIAS_TEXT_KEY, listener, prefs, res, Contact.ALIASES);
        deactivate(Notify.ALIAS_TEXT_KEY, act);
        setupCorePref(Calculate.ALIAS_TEXT_KEY, listener, prefs, res, Calculate.ALIASES);
        setupCorePref(Weather.ALIAS_TEXT_KEY, listener, prefs, res, Weather.ALIASES);
        setupCorePref(Bookmark.ALIAS_TEXT_KEY, listener, prefs, res, Bookmark.ALIASES);
        setupCorePref(Torch.ALIAS_TEXT_KEY, listener, prefs, res, Torch.ALIASES);
        setupCorePref(Info.ALIAS_TEXT_KEY, listener, prefs, res, Info.ALIASES);
        setupCorePref(Toast.ALIAS_TEXT_KEY, listener, prefs, res, Toast.ALIASES);
        deactivate("aliases_custom_text", act);
    }

    private void setupCorePref(String textKey, OnPreferenceChangeListener listener,
            SharedPreferences prefs, Resources res, @ArrayRes int setId) {
        EditTextPreference cmdPref = findPreference(textKey);
        if (cmdPref == null) return;
        String setKey = textKey.substring(0, textKey.length() - 5);
        setupPref(cmdPref, setKey, listener, prefs, res, setId);
    }

    private void setupPref(EditTextPreference cmdPref, String setKey,
            OnPreferenceChangeListener listener, SharedPreferences prefs,
            Resources res, @ArrayRes int setId) {
        Set<String> aliases = prefs.getStringSet(setKey,  Set.of(res.getStringArray(setId)));
        cmdPref.setText(String.join(", ", aliases));
        cmdPref.setOnPreferenceChangeListener(listener);
    }

    private void deactivate(String textKey, EmillaActivity act) {
        Preference cmdPref = findPreference(textKey);
        if (cmdPref != null) cmdPref.setOnPreferenceClickListener(pref -> {
            act.toast("Coming soon!");
            return false;
        });
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
}