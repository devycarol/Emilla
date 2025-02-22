package net.emilla.config;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

import net.emilla.EmillaActivity;
import net.emilla.R;
import net.emilla.chime.Chimer;
import net.emilla.settings.SettingVals;
import net.emilla.system.EmillaAccessibilityService;
import net.emilla.util.Apps;
import net.emilla.util.Features;

public final class SettingsFragment extends EmillaPreferenceFragment {

    private static final String
            EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key",
            EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":settings:show_fragment_args";

//    @StringRes
//    private static int resourceOf(String prefKey) {
//        return switch (prefKey) {
//        case SettingVals.CHIME_START -> R.string.chime_start;
//        case SettingVals.CHIME_ACT -> R.string.chime_act;
//        case SettingVals.CHIME_PEND -> R.string.chime_pend;
//        case SettingVals.CHIME_RESUME -> R.string.chime_resume;
//        case SettingVals.CHIME_EXIT -> R.string.chime_exit;
//        case SettingVals.CHIME_SUCCEED -> R.string.chime_succeed;
//        case SettingVals.CHIME_FAIL -> R.string.chime_fail;
//        default -> -1;
//        };
//    }

    private EmillaActivity mActivity;
    private SharedPreferences mPrefs;
//    private Resources mRes;
    private PackageManager mPm;

    private boolean mCustomSounds;
    private ActivityResultLauncher<Intent> mResultLauncher;
    private String mPrefKey;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs, rootKey);

        mActivity = emillaActivity();
        mPrefs = prefs();
//        mRes = getResources();
        mPm = mActivity.getPackageManager();

        mResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data == null) return;
                Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                mPrefs.edit().putString(mPrefKey, uri == null ? null : uri.toString()).apply();
                updateCustomSoundPref(mPrefKey, true/*, resourceOf(mPrefKey)*/);
            }
        });

        setupSoundSetPref();
        mCustomSounds = SettingVals.soundSet(mPrefs).equals(Chimer.CUSTOM);
        setupCustomSoundPrefs(mCustomSounds);
        setupFavoriteCommandsPref();
        boolean noTorch = !Features.torch(mPm);
        setupActionPref(preferenceOf("action_no_command"), noTorch, true);
        setupDoubleAssistPref(noTorch);
        setupActionPref(preferenceOf("action_long_submit"), noTorch, false);
        setupActionPref(preferenceOf("action_menu"), noTorch, false);
        setupDefaultAssistantPref();
        setupNotificationsPref();
        setupAccessibilityButtonPref();
        setupAppInfoPref();
    }

    private void setupSoundSetPref() {
        Preference soundSetPref = preferenceOf(Chimer.SOUND_SET);
        soundSetPref.setOnPreferenceChangeListener((pref, newVal) -> {
            boolean customSounds = newVal.equals(Chimer.CUSTOM);
            if (mCustomSounds != customSounds) {
                updateCustomSoundPrefs(customSounds);
                mCustomSounds = customSounds;
            }
            return true;
        });
    }

    private void setupCustomSoundPrefs(boolean enabled) {
        setupCustomSoundPref(Chimer.PREF_START, enabled/*, R.string.chime_start*/);
        setupCustomSoundPref(Chimer.PREF_ACT, enabled/*, R.string.chime_act*/);
        setupCustomSoundPref(Chimer.PREF_PEND, enabled/*, R.string.chime_pend*/);
        setupCustomSoundPref(Chimer.PREF_RESUME, enabled/*, R.string.chime_resume*/);
        setupCustomSoundPref(Chimer.PREF_EXIT, enabled/*, R.string.chime_exit*/);
        setupCustomSoundPref(Chimer.PREF_SUCCEED, enabled/*, R.string.chime_succeed*/);
        setupCustomSoundPref(Chimer.PREF_FAIL, enabled/*, R.string.chime_fail*/);
    }

    private void setupCustomSoundPref(
        String prefKey,
        boolean enabled/*,
        @StringRes int resId*/
    ) {
        Preference soundPref = preferenceOf(prefKey);
        soundPref.setOnPreferenceClickListener((pref) -> onClickCustomSoundPref(prefKey));
        if (!enabled) soundPref.setVisible(false);
    //    soundPref.setSummaryProvider(new Preference.SummaryProvider<>() {
    //        @Nullable @Override
    //        public CharSequence provideSummary(@NonNull Preference pref) {
    //            var uriStr = mPrefs.getString(prefKey, null);
    //            if (uriStr != null) {
    //                var ringtone = RingtoneManager.getRingtone(mActivity, Uri.parse(uriStr));
    //                if (ringtone != null) return ringtone.getTitle(mActivity);
    //            }
    //            return Lang.wordConcat(mRes, R.string.sound_set_nebula, resId);
    //        }
    //    });
        // Too laggy for now.
    }

    private boolean onClickCustomSoundPref(String prefKey) {
        var soundPicker = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
                .putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
                .putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
                .putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
        var uriStr = mPrefs.getString(prefKey, null);
        soundPicker.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                uriStr != null ? Uri.parse(uriStr) : null);
        mPrefKey = prefKey;
        mResultLauncher.launch(soundPicker);
        return false;
    }

    private void updateCustomSoundPrefs(boolean enabled) {
        updateCustomSoundPref(Chimer.PREF_START, enabled/*, R.string.chime_start*/);
        updateCustomSoundPref(Chimer.PREF_ACT, enabled/*, R.string.chime_act*/);
        updateCustomSoundPref(Chimer.PREF_PEND, enabled/*, R.string.chime_pend*/);
        updateCustomSoundPref(Chimer.PREF_RESUME, enabled/*, R.string.chime_resume*/);
        updateCustomSoundPref(Chimer.PREF_EXIT, enabled/*, R.string.chime_exit*/);
        updateCustomSoundPref(Chimer.PREF_SUCCEED, enabled/*, R.string.chime_succeed*/);
        updateCustomSoundPref(Chimer.PREF_FAIL, enabled/*, R.string.chime_fail*/);
    }

    private void updateCustomSoundPref(String prefKey, boolean enabled) {
        Preference soundPref = preferenceOf(prefKey);
        soundPref.setVisible(enabled);
    }

    private void setupFavoriteCommandsPref() {
        Preference favoriteCommmands = preferenceOf("favorite_commands");
        favoriteCommmands.setOnPreferenceClickListener(pref -> caveat("Coming soon!", false)); // todo
    }

    private boolean mShouldToast = true;
    private boolean setupActionPref(ListPreference actionPref, boolean noTorch, boolean noSelectAll) {
        if (noTorch) {
            var entries = noSelectAll ? new CharSequence[]{"None", "These settings"}
                    : new CharSequence[]{"None", "These settings", "Select whole command"}; // TODO LANG: remove
            actionPref.setEntries(entries);
            CharSequence[] values = {"none", "config", "select_all"};
            actionPref.setEntryValues(values);
        }
        actionPref.setOnPreferenceClickListener(pref -> { // todo
            if (mShouldToast) {
                caveat("More actions (commands ;) coming soon!", true);
                mShouldToast = false;
            }
            return false;
        });
        return true;
    }

    private void setupDoubleAssistPref(boolean noTorch) {
        ListPreference doubleAction = preferenceOf("action_double_assist");
        if (setupActionPref(doubleAction, noTorch, false) && noTorch) doubleAction.setDefaultValue("config");
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
            var notifSettings = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, Apps.MY_PKG);
            if (notifSettings.resolveActivity(mPm) != null) {
                appNotifications.setIntent(notifSettings);
                return;
            }
        }
        appNotifications.setVisible(false);
    }

    private void setupAccessibilityButtonPref() {
        Preference accessibilityButton = preferenceOf("accessibility_button");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var showArgs = Apps.MY_PKG + "/" + EmillaAccessibilityService.class.getName();
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
        Intent in = Apps.infoTask();
        if (in.resolveActivity(mPm) != null) systemAppInfo.setIntent(in);
        else systemAppInfo.setVisible(false);
    }

    private boolean caveat(CharSequence text, boolean longToast) { // Todo: remove these
        mActivity.toast(text, longToast);
        return false;
    }
}