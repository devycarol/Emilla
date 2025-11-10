package net.emilla.config;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

import net.emilla.R;
import net.emilla.action.QuickAction;
import net.emilla.activity.EmillaActivity;
import net.emilla.chime.Chime;
import net.emilla.chime.Chimer;
import net.emilla.command.core.CoreEntry;
import net.emilla.lang.Lang;
import net.emilla.result.ChimeSoundResult;
import net.emilla.result.GetChimeSound;
import net.emilla.system.EmillaA11yService;
import net.emilla.util.Apps;
import net.emilla.util.Features;
import net.emilla.util.Intents;

public final class SettingsFragment extends EmillaSettingsFragment {

    private static final String EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key";
    private static final String EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":settings:show_fragment_args";

    private EmillaActivity mActivity;
    private SharedPreferences mPrefs;
    private Resources mRes;
    private PackageManager mPm;

    private boolean mUsingCustomSounds;

    private final ActivityResultLauncher<Chime> mSoundPickerLauncher = registerForActivityResult(
        new GetChimeSound(),
        this::onPickChimeSound
    );

    private void onPickChimeSound(ChimeSoundResult chimeSoundResult) {
        if (chimeSoundResult == null) return;

        Chime chime = chimeSoundResult.chime;
        Uri soundUri = chimeSoundResult.soundUri;

        if (soundUri != null) {
            SettingVals.setCustomSound(mPrefs, chime, soundUri);
        } else {
            SettingVals.deleteCustomChimeSound(mPrefs, chime);
        }

        preferenceOf(chime.preferenceKey).setSummary(customSoundTitle(chime));
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs, rootKey);

        mActivity = emillaActivity();
        mPrefs = prefs();
        mRes = getResources();
        mPm = mActivity.getPackageManager();

        setupDefaultCommandPref();

        setupChimerPref();
        mUsingCustomSounds = SettingVals.chimerId(mPrefs).equals(Chimer.CUSTOM);
        setupCustomSoundPrefs(mUsingCustomSounds);
        setupFavoriteCommandsPref();

        if (!Features.torch(mPm)) {
            removeTorch(preferenceOf(QuickAction.PREF_NO_COMMAND));
            removeTorch(preferenceOf(QuickAction.PREF_DOUBLE_ASSIST));
            removeTorch(preferenceOf(QuickAction.PREF_LONG_SUBMIT));
            removeTorch(preferenceOf(QuickAction.PREF_MENU_KEY));
        }

        setupDefaultAssistantPref();
        setupNotificationsPref();
        setupAccessibilityButtonPref();
        setupAppInfoPref();
    }

    private void setupDefaultCommandPref() {
        ListPreference defaultCommand = preferenceOf(SettingVals.DEFAULT_COMMAND);
        defaultCommand.setEntries(CoreEntry.entryNames(mRes));
        defaultCommand.setEntryValues(CoreEntry.entryValues());
        // todo: allow apps
    }

    private void setupChimerPref() {
        preferenceOf(SettingVals.CHIMER).setOnPreferenceChangeListener(
            (pref, newVal) -> {
                boolean usingCustomSounds = newVal.equals(Chimer.CUSTOM);

                if (mUsingCustomSounds != usingCustomSounds) {
                    for (var chime : Chime.values()) {
                        Preference customSoundPref = preferenceOf(chime.preferenceKey);
                        customSoundPref.setVisible(usingCustomSounds);
                        activateCustomSoundPref(customSoundPref, chime);
                    }
                    mUsingCustomSounds = usingCustomSounds;
                }

                return true;
            }
        );
    }

    private void setupCustomSoundPrefs(boolean isEnabled) {
        for (var chime : Chime.values()) {
            Preference customSoundPref = preferenceOf(chime.preferenceKey);
            if (isEnabled) {
                activateCustomSoundPref(customSoundPref, chime);
            } else {
                customSoundPref.setVisible(false);
            }
        }
    }

    private void activateCustomSoundPref(Preference customSoundPref, Chime chime) {
        customSoundPref.setOnPreferenceClickListener(pref -> {
            mSoundPickerLauncher.launch(chime);
            return false;
        });

        customSoundPref.setSummary(customSoundTitle(chime));
    }

    private String customSoundTitle(Chime chime) {
        Uri soundUri = SettingVals.customChimeSoundUri(mPrefs, chime);

        if (soundUri != null) {
            Ringtone ringtone = RingtoneManager.getRingtone(mActivity, soundUri);
            if (ringtone != null) {
                return ringtone.getTitle(mActivity);
            }
        }

        return Lang.wordConcat(mRes, R.string.sound_set_nebula, chime.name);
    }

    private void setupFavoriteCommandsPref() {
        Preference favoriteCommmands = preferenceOf("favorite_commands");
        favoriteCommmands.setOnPreferenceClickListener(pref -> {
            mActivity.toast("Coming soon!", false);
            // Todo
            return false;
        });
    }

    private static void removeTorch(ListPreference actionPref) {
        if (actionPref.getValue().equals(QuickAction.FLASHLIGHT)) {
            actionPref.setValue(QuickAction.ASSISTANT_SETTINGS);
            actionPref.setDefaultValue(QuickAction.ASSISTANT_SETTINGS);
        }

        CharSequence[] entries = actionPref.getEntries();
        CharSequence[] values = actionPref.getEntryValues();

        int index = -1;

        int entryCount = entries.length;
        for (int i = 0; i < entryCount; ++i) {
            if (QuickAction.FLASHLIGHT.equals(values[i].toString())) {
                index = i;
                break;
            }
        }

        actionPref.setEntries(arrayWithout(entries, index));
        actionPref.setEntryValues(arrayWithout(values, index));
    }

    private static CharSequence[] arrayWithout(CharSequence[] array, int index) {
        int last = array.length - 1;
        var without = new CharSequence[last];

        System.arraycopy(array, 0, without, 0, index);
        System.arraycopy(array, index + 1, without, index, last - index);

        return without;
    }

    private void setupDefaultAssistantPref() {
        // todo: whatever sneaky nonsense the g assistant uses to highlight system settings should also be used here
        Preference systemDefaultAssistant = preferenceOf("default_assistant");
        var assistantSettings = new Intent(Settings.ACTION_VOICE_INPUT_SETTINGS);
        if (assistantSettings.resolveActivity(mPm) != null) {
            systemDefaultAssistant.setIntent(assistantSettings);
            return;
        }
        systemDefaultAssistant.setVisible(false);
    }

    private void setupNotificationsPref() {
        Preference appNotifications = preferenceOf("notifications");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent notificationSettings = Intents.notificationSettings();
            if (notificationSettings.resolveActivity(mPm) != null) {
                appNotifications.setIntent(notificationSettings);
                return;
            }
        }
        appNotifications.setVisible(false);
    }

    private void setupAccessibilityButtonPref() {
        Preference accessibilityButton = preferenceOf("accessibility_button");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String showArgs = Apps.MY_PKG + '/' + EmillaA11yService.class.getName();
            var bundle = new Bundle();
            bundle.putString(EXTRA_FRAGMENT_ARG_KEY, showArgs);
            var in = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, Apps.MY_PKG)
                    .putExtra(EXTRA_FRAGMENT_ARG_KEY, showArgs)
                    .putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle);
            if (in.resolveActivity(mPm) != null) {
                accessibilityButton.setIntent(in);
                return;
            }
        }
        accessibilityButton.setVisible(false);
    }

    private void setupAppInfoPref() {
        Preference systemAppInfo = preferenceOf("app_info");
        Intent appInfo = Intents.appInfo();
        if (appInfo.resolveActivity(mPm) != null) {
            systemAppInfo.setIntent(appInfo);
        } else {
            systemAppInfo.setVisible(false);
        }
    }

}