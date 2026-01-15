package net.emilla.config;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import net.emilla.R;
import net.emilla.util.Toasts;

public final class LayoutSettingsFragment extends PreferenceFragmentCompat {
    public LayoutSettingsFragment() {
        super();
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.settings_layout, rootKey);

        var ctx = requireContext();
        setupFavoriteCommandsPref(ctx);
    }

    private void setupFavoriteCommandsPref(Context ctx) {
        Preference favoriteCommands = findPreference("favorite_commands");
        favoriteCommands.setOnPreferenceClickListener(pref -> {
            Toasts.show(ctx, "Coming soon!");
            // Todo
            return false;
        });
    }
}
