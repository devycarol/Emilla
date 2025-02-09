package net.emilla.config;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;

import net.emilla.EmillaActivity;
import net.emilla.R;
import net.emilla.command.app.AospContacts;
import net.emilla.command.app.AppCommand;
import net.emilla.command.app.Discord;
import net.emilla.command.app.Firefox;
import net.emilla.command.app.Github;
import net.emilla.command.app.Markor;
import net.emilla.command.app.Newpipe;
import net.emilla.command.app.Outlook;
import net.emilla.command.app.Signal;
import net.emilla.command.app.Tasker;
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
import net.emilla.command.core.Setting;
import net.emilla.command.core.Share;
import net.emilla.command.core.Sms;
import net.emilla.command.core.Snippets;
import net.emilla.command.core.Time;
import net.emilla.command.core.Timer;
import net.emilla.command.core.Toast;
import net.emilla.command.core.Todo;
import net.emilla.command.core.Torch;
import net.emilla.command.core.Weather;
import net.emilla.command.core.Web;
import net.emilla.settings.SettingVals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CommandsFragment extends EmillaPreferenceFragment {

    private EmillaActivity mActivity;
    private SharedPreferences mPrefs;
    private Resources mRes;
    private PackageManager mPm;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.command_prefs, rootKey);

        mActivity = emillaActivity();
        mPrefs = prefs();
        mRes = getResources();
        mPm = mActivity.getPackageManager();

        OnPreferenceChangeListener listener = (pref, newVal) -> {
            String textKey = pref.getKey();
            String setKey = textKey.substring(0, textKey.length() - 5);
            String correctedText = ((String) newVal).trim().toLowerCase();
            String[] vals = (correctedText.split(" *, *"));
            Set<String> aliases = Set.of(vals);
            String joined = String.join(", ", aliases);
            ((EditTextPreference) pref).setText(joined);
            mPrefs.edit()
                  .putString(textKey, joined)
                  .putStringSet(setKey, aliases)
                  .apply();
            return false;
        };

        setupCores(listener);
        setupApps(listener);
        setupCustoms();
    }

    private void setupCores(OnPreferenceChangeListener listener) {
        setupCorePref(Call.ALIAS_TEXT_KEY, listener, Call.ALIASES);
        setupCorePref(Dial.ALIAS_TEXT_KEY, listener, Dial.ALIASES);
        setupCorePref(Sms.ALIAS_TEXT_KEY, listener, Sms.ALIASES);
        setupCorePref(Email.ALIAS_TEXT_KEY, listener, Email.ALIASES);
        setupCorePref(Copy.ALIAS_TEXT_KEY, listener, Copy.ALIASES);
        setupCorePref(Snippets.ALIAS_TEXT_KEY, listener, Snippets.ALIASES);
        setupCorePref(Share.ALIAS_TEXT_KEY, listener, Share.ALIASES);
        setupCorePref(Launch.ALIAS_TEXT_KEY, listener, Launch.ALIASES);
        deactivate(Setting.ALIAS_TEXT_KEY);
        deactivate(Note.ALIAS_TEXT_KEY);
        deactivate(Todo.ALIAS_TEXT_KEY);
        setupCorePref(Web.ALIAS_TEXT_KEY, listener, Web.ALIASES);
        deactivate(Find.ALIAS_TEXT_KEY);
        setupCorePref(Time.ALIAS_TEXT_KEY, listener, Time.ALIASES);
        setupCorePref(Alarm.ALIAS_TEXT_KEY, listener, Alarm.ALIASES);
        setupCorePref(Timer.ALIAS_TEXT_KEY, listener, Timer.ALIASES);
        setupCorePref(Pomodoro.ALIAS_TEXT_KEY, listener, Pomodoro.ALIASES);
        setupCorePref(Calendar.ALIAS_TEXT_KEY, listener, Calendar.ALIASES);
        setupCorePref(Contact.ALIAS_TEXT_KEY, listener, Contact.ALIASES);
        deactivate(Notify.ALIAS_TEXT_KEY);
        setupCorePref(Calculate.ALIAS_TEXT_KEY, listener, Calculate.ALIASES);
        setupCorePref(Weather.ALIAS_TEXT_KEY, listener, Weather.ALIASES);
        setupCorePref(Bookmark.ALIAS_TEXT_KEY, listener, Bookmark.ALIASES);
        setupCorePref(Torch.ALIAS_TEXT_KEY, listener, Torch.ALIASES);
        setupCorePref(Info.ALIAS_TEXT_KEY, listener, Info.ALIASES);
        setupCorePref(Toast.ALIAS_TEXT_KEY, listener, Toast.ALIASES);
    }

    private void setupCorePref(String textKey, OnPreferenceChangeListener listener,
            @ArrayRes int aliases) {
        EditTextPreference cmdPref = preferenceOf(textKey);
        String setKey = textKey.substring(0, textKey.length() - 5);
        setupPref(cmdPref, setKey, listener, mRes, aliases);
    }

    private void setupPref(EditTextPreference cmdPref, String setKey,
            OnPreferenceChangeListener listener, Resources res, @ArrayRes int aliases) {
        Set<String> aliasSet = mPrefs.getStringSet(setKey, Set.of(res.getStringArray(aliases)));
        cmdPref.setText(String.join(", ", aliasSet));
        cmdPref.setOnPreferenceChangeListener(listener);
    }

    private void deactivate(String textKey) {
        Preference cmdPref = preferenceOf(textKey);
        cmdPref.setOnPreferenceClickListener(pref -> {
            mActivity.toast("Coming soon!");
            return false;
        });
    }

    private void setupApps(OnPreferenceChangeListener listener) {
        setupAppPref(AospContacts.PKG, listener);
        setupAppPref(Markor.PKG, listener);
        setupAppPref(Firefox.PKG, listener);
        setupAppPref(Tor.PKG, listener);
        setupAppPref(Signal.PKG, listener);
        setupAppPref(Newpipe.PKG, listener);
        setupAppPref(Tubular.PKG, listener);
        setupAppPref(Tasker.PKG, listener);
        setupAppPref(Github.PKG, listener);
        setupAppPref(Youtube.PKG, listener);
        setupAppPref(Discord.PKG, listener);
        setupAppPref(Outlook.PKG, listener);
        // Todo: procedurally generate these
    }

    private void setupAppPref(String pkg, OnPreferenceChangeListener listener) {
        EditTextPreference appCmdPref = preferenceOf("aliases_" + pkg + "_text");
    try {
        ApplicationInfo info = mPm.getApplicationInfo(pkg, 0);
        CharSequence label = mPm.getApplicationLabel(info);
        appCmdPref.setTitle(label);
        // this uses the application icon and doesn't account for multiple launcher icons yet
        Drawable appIcon = mPm.getApplicationIcon(pkg);
        appCmdPref.setIcon(appIcon);
        setupPref(appCmdPref, "aliases_" + pkg, listener, mRes, AppCommand.aliasId(pkg, Markor.CLS_MAIN /*Todo: procedurally generate these prefs*/));
    } catch (PackageManager.NameNotFoundException e) {
        appCmdPref.setVisible(false);
    }}

    private void setupCustoms() {
        EditTextPreference customCommands = preferenceOf(SettingVals.ALIASES_CUSTOM_TEXT);
        customCommands.setOnPreferenceChangeListener((pref, newVal) -> {
            // self-evident Todo.
            String newText = (String) newVal;

            StringBuilder reviseBldr = new StringBuilder();
            Set<String> customEntries = new HashSet<>();
            for (String entry : newText.split(" *\n *")) {
                String revisedEntry = cleanCommaList(entry);
                if (revisedEntry != null) {
                    reviseBldr.append(revisedEntry).append('\n');
                    customEntries.add(revisedEntry);
                }
                else reviseBldr.append('\n');
            }

            int len = reviseBldr.length();
            if (len > 0) reviseBldr.setLength(len - 1);
            // snip trailing newline

            String revisedText = reviseBldr.toString();
            ((EditTextPreference) pref).setText(revisedText);

            mPrefs.edit()
                  .putString(SettingVals.ALIASES_CUSTOM_TEXT, revisedText)
                  .putStringSet(SettingVals.ALIASES_CUSTOM, customEntries)
                  .apply();

            return false;
        });
    }

    @Nullable
    private static String cleanCommaList(String entry) {
        String[] split = entry.split("( *, *)+");
        int len = split.length;
        if (len >= 2) {
            List<String> items = new ArrayList<>(len);

            int i = 0;
            do if (!split[i].isEmpty()) items.add(split[i]);
            while (++i < len);

            if (items.size() >= 2) return String.join(", ", items);
        }

        return null;
    }
}