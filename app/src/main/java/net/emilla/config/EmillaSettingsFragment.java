package net.emilla.config;

import android.content.SharedPreferences;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Objects;

public abstract class EmillaSettingsFragment extends PreferenceFragmentCompat {

    protected final <T extends Preference> T preferenceOf(String key) {
        return Objects.requireNonNull(findPreference(key));
    }

    protected final SharedPreferences prefs() {
        return Objects.requireNonNull(getPreferenceManager().getSharedPreferences());
    }

}
