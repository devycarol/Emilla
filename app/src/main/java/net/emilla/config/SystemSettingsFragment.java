package net.emilla.config;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import net.emilla.R;
import net.emilla.system.EmillaA11yService;
import net.emilla.util.Apps;
import net.emilla.util.Intents;

public final class SystemSettingsFragment extends PreferenceFragmentCompat {
    private static final String EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key";
    private static final String EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":settings:show_fragment_args";

    public SystemSettingsFragment() {
        super();
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.settings_system, rootKey);

        var ctx = requireContext();
        var pm = ctx.getPackageManager();
        setupAppInfo(pm);
        setupNotifications(pm);
        setupDefaultAssistant(pm);
        setupAccessibilityButton(pm);
    }

    private void setupAppInfo(PackageManager pm) {
        Intent appInfo = Intents.appInfo();
        Preference link = findPreference("app_info");
        if (appInfo.resolveActivity(pm) != null) {
            link.setIntent(appInfo);
            return;
        }

        link.setVisible(false);
    }

    private void setupNotifications(PackageManager pm) {
        Preference link = findPreference("notifications");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent pingSettings = Intents.notificationSettings();
            if (pingSettings.resolveActivity(pm) != null) {
                link.setIntent(pingSettings);
                return;
            }
        }

        link.setVisible(false);
    }

    private void setupDefaultAssistant(PackageManager pm) {
        // todo: whatever sneaky nonsense the g assistant uses to highlight system settings should also be used here
        Preference link = findPreference("default_assistant");
        var a6ntSettings = new Intent(Settings.ACTION_VOICE_INPUT_SETTINGS);
        if (a6ntSettings.resolveActivity(pm) != null) {
            link.setIntent(a6ntSettings);
            return;
        }

        link.setVisible(false);
    }

    private void setupAccessibilityButton(PackageManager pm) {
        Preference link = findPreference("accessibility_button");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent a11ySettings = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            hooliganActivities(a11ySettings);

            if (a11ySettings.resolveActivity(pm) != null) {
                link.setIntent(a11ySettings);
                return;
            }
        }

        link.setVisible(false);
    }

    private static void hooliganActivities(Intent intent) {
        var bundle = new Bundle();
        String showArgs = Apps.MY_PKG + '/' + EmillaA11yService.class.getName();
        bundle.putString(EXTRA_FRAGMENT_ARG_KEY, showArgs);

        intent
            .putExtra(Settings.EXTRA_APP_PACKAGE, Apps.MY_PKG)
            .putExtra(EXTRA_FRAGMENT_ARG_KEY, showArgs)
            .putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle)
        ;
    }
}
