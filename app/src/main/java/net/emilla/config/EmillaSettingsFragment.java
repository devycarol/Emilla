package net.emilla.config;

import android.content.SharedPreferences;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import net.emilla.annotation.internal;

import java.util.Objects;

abstract class EmillaSettingsFragment extends PreferenceFragmentCompat {
    @internal EmillaSettingsFragment() {}

    protected final <T extends Preference> T preferenceOf(CharSequence key) {
        return Objects.requireNonNull(findPreference(key));
    }

    protected final SharedPreferences prefs() {
        return Objects.requireNonNull(getPreferenceManager().getSharedPreferences());
    }
}
