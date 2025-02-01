package net.emilla.config;

import static java.util.Objects.requireNonNull;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import net.emilla.EmillaActivity;

public abstract class EmillaPreferenceFragment extends PreferenceFragmentCompat {

    @NonNull
    protected final <T extends Preference> T preferenceOf(@NonNull String key) {
        return requireNonNull(findPreference(key));
    }

    @NonNull
    protected final EmillaActivity emillaActivity() {
        return (EmillaActivity) requireActivity();
    }

    @NonNull
    protected final SharedPreferences prefs() {
        return requireNonNull(getPreferenceManager().getSharedPreferences());
    }
}
