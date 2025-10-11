package net.emilla.config;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceCategory;

import net.emilla.R;
import net.emilla.activity.EmillaActivity;
import net.emilla.app.AppEntry;
import net.emilla.app.AppList;
import net.emilla.command.core.Alarm;
import net.emilla.command.core.Bits;
import net.emilla.command.core.Calculate;
import net.emilla.command.core.Calendar;
import net.emilla.command.core.Call;
import net.emilla.command.core.Celsius;
import net.emilla.command.core.Contact;
import net.emilla.command.core.Copy;
import net.emilla.command.core.CoreCommand;
import net.emilla.command.core.Dial;
import net.emilla.command.core.Email;
import net.emilla.command.core.Fahrenheit;
import net.emilla.command.core.Find;
import net.emilla.command.core.Info;
import net.emilla.command.core.Launch;
import net.emilla.command.core.Note;
import net.emilla.command.core.Notifications;
import net.emilla.command.core.Notify;
import net.emilla.command.core.Pause;
import net.emilla.command.core.Play;
import net.emilla.command.core.Pomodoro;
import net.emilla.command.core.RandomNumber;
import net.emilla.command.core.Roll;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public final class CommandsFragment extends EmillaSettingsFragment {

    private EmillaActivity mActivity;
    private PackageManager mPm;
    private SharedPreferences mPrefs;
    private Resources mRes;

    private final OnPreferenceChangeListener mListener = (pref, newVal) -> {
        var cmdPref = (CommandPreference) pref;
        String textKey = cmdPref.getKey();
        String setKey = cmdPref.setKey;
        String correctedText = ((String) newVal).trim().toLowerCase();
        String[] vals = correctedText.split(" *, *");
        Set<String> aliases = Set.of(vals);
        var joined = String.join(", ", aliases);
        cmdPref.setText(joined);
        mPrefs.edit()
              .putString(textKey, joined)
              .putStringSet(setKey, aliases)
              .apply();
        return false;
    };

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.command_prefs, rootKey);

        mActivity = emillaActivity();
        mPm = mActivity.getPackageManager();
        mPrefs = prefs();
        mRes = getResources();

        setupCores();
        setupApps();
        setupCustoms();
    }

    private void setupCores() {
        setupCorePref(Call.ENTRY, Call.ALIASES);
        setupCorePref(Dial.ENTRY, Dial.ALIASES);
        setupCorePref(Sms.ENTRY, Sms.ALIASES);
        setupCorePref(Email.ENTRY, Email.ALIASES);
        setupCorePref(Copy.ENTRY, Copy.ALIASES);
        setupCorePref(Snippets.ENTRY, Snippets.ALIASES);
        setupCorePref(Share.ENTRY, Share.ALIASES);
        setupCorePref(Launch.ENTRY, Launch.ALIASES);
        deactivate(Setting.ENTRY, false);
        deactivate(Note.ENTRY, false);
        deactivate(Todo.ENTRY, false);
        setupCorePref(Web.ENTRY, Web.ALIASES);
        deactivate(Find.ENTRY, false);
        setupCorePref(Time.ENTRY, Time.ALIASES);
        setupCorePref(Alarm.ENTRY, Alarm.ALIASES);
        setupCorePref(Timer.ENTRY, Timer.ALIASES);
        setupCorePref(Pomodoro.ENTRY, Pomodoro.ALIASES);
        setupCorePref(Calendar.ENTRY, Calendar.ALIASES);
        setupCorePref(Contact.ENTRY, Contact.ALIASES);
        setupCorePref(Notify.ENTRY, Notify.ALIASES);
        setupCorePref(Calculate.ENTRY, Calculate.ALIASES);
        setupCorePref(RandomNumber.ENTRY, RandomNumber.ALIASES);
        setupCorePref(Celsius.ENTRY, Celsius.ALIASES);
        setupCorePref(Fahrenheit.ENTRY, Fahrenheit.ALIASES);
        setupCorePref(Roll.ENTRY, Roll.ALIASES);
        setupCorePref(Bits.ENTRY, Bits.ALIASES);
        setupCorePref(Weather.ENTRY, Weather.ALIASES);
        setupCorePref(Play.ENTRY, Play.ALIASES);
        setupCorePref(Pause.ENTRY, Pause.ALIASES);
        setupCorePref(Torch.ENTRY, Torch.ALIASES);
        setupCorePref(Info.ENTRY, Info.ALIASES);
        setupCorePref(Notifications.ENTRY, Notifications.ALIASES);
        setupCorePref(Toast.ENTRY, Toast.ALIASES);
    }

    private void setupCorePref(
        String entry,
        @ArrayRes int aliases
    ) {
        String enabledKey = SettingVals.commandEnabledKey(entry);
        if (CoreCommand.possible(mPm, entry)) {
            if (!mPrefs.contains(enabledKey)) {
                mPrefs.edit().putBoolean(enabledKey, true).apply();
                // TODO: actually allow to toggle this setting.
            }
            String textKey = Aliases.textKey(entry);
            CommandPreference cmdPref = preferenceOf(textKey);
            Set<String> aliasSet = Aliases.coreSet(mPrefs, mRes, entry, aliases);
            setupPref(cmdPref, aliasSet);
        } else {
            mPrefs.edit().putBoolean(enabledKey, false).apply();
            deactivate(entry, true);
        }
    }

    private void setupPref(
        CommandPreference cmdPref,
        @Nullable Set<String> aliases
    ) {
        cmdPref.setText(aliases != null ? String.join(", ", aliases) : null);
        cmdPref.setOnPreferenceChangeListener(mListener);
    }

    private void deactivate(String entry, boolean implemented) {
        CommandPreference cmdPref = preferenceOf(Aliases.textKey(entry));
        cmdPref.setEnabled(false);
//        if (implemented) cmdPref.setOnPreferenceClickListener(pref -> {
//            mActivity.toast(R.string.toast_command_unsupported);
//            /*Your device doesn\'t support this command.*/
//               TODO: this doesn't work because it's EditTextPreference
//               Todo: offer to search for apps that may satisfy the command or inform that it's a
//                hardware issue.
//            return false;
//        });
//        else cmdPref.setOnPreferenceClickListener(pref -> {
//            mActivity.toast("Coming soon!");
//            return false;
//        });
    }

    private void setupApps() {
        // Todo: priority-sort the apps with hard-coded support?
        PreferenceCategory apps = preferenceOf("category_apps");
        for (AppEntry app : AppList.launchers(mPm)) {
            var appPref = new CommandPreference(mActivity, app);
            apps.addPreference(appPref);
            setupPref(appPref, Aliases.appSet(mPrefs, mRes, app));
        }
    }

    private void setupCustoms() {
        EditTextPreference customCommands = preferenceOf(SettingVals.ALIASES_CUSTOM_TEXT);
        if (true) {
            customCommands.setEnabled(false);
            return;
        }
        customCommands.setOnPreferenceChangeListener((pref, newVal) -> {
            // self-evident Todo.
            var newText = (String) newVal;

            var reviseBldr = new StringBuilder();
            var customEntries = new HashSet<String>();
            for (String entry : newText.split(" *\n *")) {
                String revisedEntry = cleanCommaList(entry);
                if (revisedEntry != null) {
                    reviseBldr.append(revisedEntry).append('\n');
                    customEntries.add(revisedEntry);
                } else {
                    reviseBldr.append('\n');
                }
            }

            int len = reviseBldr.length();
            if (len > 0) reviseBldr.setLength(len - 1);
            // snip trailing newline

            var revisedText = reviseBldr.toString();
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
            var items = new ArrayList<String>(len);

            int i = 0;
            do if (!split[i].isEmpty()) items.add(split[i]);
            while (++i < len);

            if (items.size() >= 2) return String.join(", ", items);
        }

        return null;
    }
}