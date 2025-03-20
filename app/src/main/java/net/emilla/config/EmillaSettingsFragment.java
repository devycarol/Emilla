package net.emilla.config;

import static java.util.Objects.requireNonNull;

import android.content.SharedPreferences;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import net.emilla.activity.EmillaActivity;

public abstract class EmillaSettingsFragment extends PreferenceFragmentCompat {

    protected final <T extends Preference> T preferenceOf(String key) {
        return requireNonNull(findPreference(key));
    }

    protected final EmillaActivity emillaActivity() {
        return (EmillaActivity) requireActivity();
    }

    protected final SharedPreferences prefs() {
        return requireNonNull(getPreferenceManager().getSharedPreferences());
    }
}
