package net.emilla.config;

import android.content.SharedPreferences;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import net.emilla.activity.EmillaActivity;

import java.util.Objects;

public abstract class EmillaSettingsFragment extends PreferenceFragmentCompat {

    protected final <T extends Preference> T preferenceOf(String key) {
        return Objects.requireNonNull(findPreference(key));
    }

    protected final EmillaActivity emillaActivity() {
        return (EmillaActivity) requireActivity();
    }

    protected final SharedPreferences prefs() {
        return Objects.requireNonNull(getPreferenceManager().getSharedPreferences());
    }
}
