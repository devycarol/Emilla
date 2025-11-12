package net.emilla.config;

import android.content.Context;
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
import net.emilla.command.app.AppEntry;
import net.emilla.command.core.CoreEntry;
import net.emilla.util.AppList;
import net.emilla.util.Patterns;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class CommandsFragment extends EmillaSettingsFragment {

    private static final Pattern SQUASHING_CSV = Pattern.compile("( *, *)+");

    private /*late*/ Context mContext;
    private /*late*/ PackageManager mPm;
    private /*late*/ SharedPreferences mPrefs;
    private /*late*/ Resources mRes;

    private final OnPreferenceChangeListener mListener = (pref, newVal) -> {
        var cmdPref = (CommandPreference) pref;
        String textKey = cmdPref.getKey();
        String setKey = cmdPref.setKey;
        String correctedText = ((String) newVal).trim().toLowerCase();
        String[] vals = Patterns.TRIMMING_CSV.split(correctedText);
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

        mContext = requireContext();
        mPm = mContext.getPackageManager();
        mPrefs = prefs();
        mRes = getResources();

        setupCores();
        setupApps();
        setupCustoms();
    }

    private void setupCores() {
        Aliases.reformatCoresIfNecessary(mPrefs);

        PreferenceCategory cores = preferenceOf("category_cores");
        for (var coreEntry : CoreEntry.values()) {
            var corePref = new CommandPreference(mContext, coreEntry);
            cores.addPreference(corePref);
            setupCorePref(coreEntry, corePref);
        }
    }

    private void setupCorePref(CoreEntry coreEntry, CommandPreference corePref) {
        String entry = coreEntry.name();
        @ArrayRes int aliases = coreEntry.aliases;

        String enabledKey = SettingVals.commandEnabledKey(entry);

        if (coreEntry.isPossible(mPm)) {
            if (!mPrefs.contains(enabledKey)) {
                mPrefs.edit().putBoolean(enabledKey, true).apply();
            }
            Set<String> aliasSet = Aliases.coreSet(mPrefs, mRes, entry, aliases);
            setupPref(corePref, aliasSet);
        } else {
            mPrefs.edit().putBoolean(enabledKey, false).apply();
            corePref.setEnabled(false);
//            corePref.setOnPreferenceClickListener(pref -> {
//                Toasts.show(mContext, R.string.toast_command_unsupported);
//                /*Your device doesn\'t support this command.*/
//                // TODO: this doesn't work because it's EditTextPreference
//                // Todo: offer to search for apps that may satisfy the command or inform that
//                    it's a hardware issue.
//                return false;
//            });
        }
    }

    private void setupPref(CommandPreference cmdPref, @Nullable Set<String> aliases) {
        cmdPref.setText(aliases != null ? String.join(", ", aliases) : null);
        cmdPref.setOnPreferenceChangeListener(mListener);
    }

    private void setupApps() {
        // Todo: priority-sort the apps with hard-coded support?
        PreferenceCategory apps = preferenceOf("category_apps");
        for (AppEntry app : AppList.launchers(mPm)) {
            var appPref = new CommandPreference(mContext, app);
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
            for (String entry : Patterns.TRIMMING_LINES.split(newText)) {
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
    private static String cleanCommaList(CharSequence entry) {
        String cleanCommaList = SQUASHING_CSV.splitAsStream(entry)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.joining(", "));

        return cleanCommaList.indexOf(',') >= 0
            ? cleanCommaList
            : null;
    }

}