package net.emilla.config;

import static androidx.core.util.ObjectsCompat.requireNonNull;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import net.emilla.EmillaActivity;
import net.emilla.R;
import net.emilla.settings.SettingVals;
import net.emilla.system.EmillaAccessibilityService;
import net.emilla.utils.Apps;
import net.emilla.utils.Chime;
import net.emilla.utils.Features;

public class SettingsFragment extends PreferenceFragmentCompat {
private static final String
    EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key",
    EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":settings:show_fragment_args";

private static boolean caveat(final EmillaActivity act, final CharSequence text, final boolean longToast) { // Todo: remove these
    act.toast(text, longToast);
    return false;
}

//@StringRes
//private static int resourceOf(final String prefKey) {
//    return switch (prefKey) {
//    case SettingVals.CHIME_START -> R.string.chime_start;
//    case SettingVals.CHIME_ACT -> R.string.chime_act;
//    case SettingVals.CHIME_PEND -> R.string.chime_pend;
//    case SettingVals.CHIME_RESUME -> R.string.chime_resume;
//    case SettingVals.CHIME_EXIT -> R.string.chime_exit;
//    case SettingVals.CHIME_SUCCEED -> R.string.chime_succeed;
//    case SettingVals.CHIME_FAIL -> R.string.chime_fail;
//    default -> -1;
//    };
//}

private boolean mCustomSounds;
private ActivityResultLauncher<Intent> mResultLauncher;
private String mPrefKey;

@Override
public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
    setPreferencesFromResource(R.xml.prefs, rootKey);

    final EmillaActivity act = (EmillaActivity) requireActivity();
    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
    final Resources res = act.getResources();
    mResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            final Intent data = result.getData();
            if (data == null) return;
            final Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            prefs.edit().putString(mPrefKey, uri == null ? null : uri.toString()).apply();
            updateCustomSoundPref(/*act, prefs, res,*/ mPrefKey, true/*, resourceOf(mPrefKey)*/);
        }
    });

    setupRunInBackgroundPref(res);
    setupSoundSetPref(/*act, prefs, res*/);
    mCustomSounds = SettingVals.soundSet(prefs).equals(Chime.CUSTOM);
    setupCustomSoundPrefs(/*act,*/ prefs, /*res,*/ mCustomSounds);
    setupFavoriteCommandsPref(act);
    final PackageManager pm = act.getPackageManager();
    final boolean noTorch = !Features.torch(pm);
    setupActionPref(findPreference("action_no_command"), act, noTorch, true);
    setupDoubleAssistPref(act, noTorch);
    setupActionPref(findPreference("action_long_press_submit"), act, noTorch, false);
    setupActionPref(findPreference("action_menu"), act, noTorch, false);
    setupDefaultAssistantPref(pm);
    setupNotificationsPref(pm);
    setupAccessibilityButtonPref(pm);
    setupAppInfoPref(pm);
}

private void setupSoundSetPref(/*final Context ctxt, final SharedPreferences prefs, final Resources res*/) {
    final Preference soundSetPref = findPreference(SettingVals.SOUND_SET);
    if (soundSetPref == null) return;
    soundSetPref.setOnPreferenceChangeListener((pref, newVal) -> {
        final boolean customSounds = newVal.equals(Chime.CUSTOM);
        if (mCustomSounds != customSounds) {
            updateCustomSoundPrefs(/*ctxt, prefs, res,*/ customSounds);
            mCustomSounds = customSounds;
        }
        return true;
    });
}

private void setupCustomSoundPref(/*final Context ctxt,*/ final SharedPreferences prefs,
        /*final Resources res,*/ final String prefKey, final boolean enabled/*, @StringRes final int resId*/) {
    final Preference soundPref = findPreference(prefKey);
    if (soundPref == null) return;
    soundPref.setOnPreferenceClickListener((pref) -> onClickCustomSoundPref(prefs, prefKey));
    if (!enabled) soundPref.setVisible(false);
//    soundPref.setSummaryProvider(new Preference.SummaryProvider<>() {
//        @Nullable @Override
//        public CharSequence provideSummary(@NonNull final Preference pref) {
//            final String uriStr = prefs.getString(prefKey, null);
//            if (uriStr != null) {
//                final Ringtone ringtone = RingtoneManager.getRingtone(ctxt, Uri.parse(uriStr));
//                if (ringtone != null) {
//                    return ringtone.getTitle(ctxt);
//                }
//            }
//            return Lang.wordConcat(res, R.string.sound_set_nebula, resId);
//        }
//    });
    // Too laggy for now.
}

private boolean onClickCustomSoundPref(final SharedPreferences prefs, final String prefKey) {
    final Intent soundPicker = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
            .putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
            .putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
            .putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
    final String uriStr = prefs.getString(prefKey, null);
    soundPicker.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
            uriStr != null ? Uri.parse(uriStr) : null);
    mPrefKey = prefKey;
    mResultLauncher.launch(soundPicker);
    return false;
}

private void setupCustomSoundPrefs(/*final Context ctxt,*/ final SharedPreferences prefs,
        /*final Resources res,*/ final boolean enabled) {
    setupCustomSoundPref(/*ctxt,*/ prefs, /*res,*/ SettingVals.CHIME_START, enabled/*, R.string.chime_start*/);
    setupCustomSoundPref(/*ctxt,*/ prefs, /*res,*/ SettingVals.CHIME_ACT, enabled/*, R.string.chime_act*/);
    setupCustomSoundPref(/*ctxt,*/ prefs, /*res,*/ SettingVals.CHIME_PEND, enabled/*, R.string.chime_pend*/);
    setupCustomSoundPref(/*ctxt,*/ prefs, /*res,*/ SettingVals.CHIME_RESUME, enabled/*, R.string.chime_resume*/);
    setupCustomSoundPref(/*ctxt,*/ prefs, /*res,*/ SettingVals.CHIME_EXIT, enabled/*, R.string.chime_exit*/);
    setupCustomSoundPref(/*ctxt,*/ prefs, /*res,*/ SettingVals.CHIME_SUCCEED, enabled/*, R.string.chime_succeed*/);
    setupCustomSoundPref(/*ctxt,*/ prefs, /*res,*/ SettingVals.CHIME_FAIL, enabled/*, R.string.chime_fail*/);
}

private void updateCustomSoundPref(/*final Context ctxt, final SharedPreferences prefs,
        final Resources res,*/ final String prefKey, final boolean enabled/*, @StringRes final int resId*/) {
    final Preference soundPref = findPreference(prefKey);
    if (soundPref == null) return;
    soundPref.setVisible(enabled);
}

private void updateCustomSoundPrefs(/*final Context ctxt, final SharedPreferences prefs,
        final Resources res,*/ final boolean enabled) {
    updateCustomSoundPref(/*ctxt, prefs, res,*/ SettingVals.CHIME_START, enabled/*, R.string.chime_start*/);
    updateCustomSoundPref(/*ctxt, prefs, res,*/ SettingVals.CHIME_ACT, enabled/*, R.string.chime_act*/);
    updateCustomSoundPref(/*ctxt, prefs, res,*/ SettingVals.CHIME_PEND, enabled/*, R.string.chime_pend*/);
    updateCustomSoundPref(/*ctxt, prefs, res,*/ SettingVals.CHIME_RESUME, enabled/*, R.string.chime_resume*/);
    updateCustomSoundPref(/*ctxt, prefs, res,*/ SettingVals.CHIME_EXIT, enabled/*, R.string.chime_exit*/);
    updateCustomSoundPref(/*ctxt, prefs, res,*/ SettingVals.CHIME_SUCCEED, enabled/*, R.string.chime_succeed*/);
    updateCustomSoundPref(/*ctxt, prefs, res,*/ SettingVals.CHIME_FAIL, enabled/*, R.string.chime_fail*/);
}

private void setupRunInBackgroundPref(final Resources res) {
    final ListPreference runInBackground = findPreference("run_in_background");
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && runInBackground != null) {
        final CharSequence[] entries = {res.getString(R.string.always), res.getString(R.string.never)};
        runInBackground.setEntries(entries);
        final CharSequence[] values = {"always", "never"};
        runInBackground.setEntryValues(values);
        runInBackground.setDefaultValue("never");
    }
}

private void setupFavoriteCommandsPref(final EmillaActivity act) {
    final Preference favoriteCommmands = findPreference("favorite_commands");
    if (favoriteCommmands == null) return;
    favoriteCommmands.setOnPreferenceClickListener(pref -> caveat(act, "Coming soon!", false)); // todo
}

private boolean mShouldToast = true;
private boolean setupActionPref(final ListPreference actionPref, final EmillaActivity act,
        final boolean noTorch, final boolean noSelectAll) {
    if (actionPref == null) return false;
    if (noTorch) {
        final CharSequence[] entries = noSelectAll ? new CharSequence[]{"None", "These settings"}
                : new CharSequence[]{"None", "These settings", "Select whole command"}; // TODO LANG: remove
        actionPref.setEntries(entries);
        final CharSequence[] values = {"none", "config", "select_all"};
        actionPref.setEntryValues(values);
    }
    actionPref.setOnPreferenceClickListener(pref -> { // todo
        if (mShouldToast) {
            caveat(act, "More actions (commands ;) coming soon!", true);
            mShouldToast = false;
        }
        return false;
    });
    return true;
}

private void setupDoubleAssistPref(final EmillaActivity act, final boolean noTorch) {
    final ListPreference doubleAction = findPreference("action_double_assist");
    if (setupActionPref(doubleAction, act, noTorch, false) && noTorch) doubleAction.setDefaultValue("config");
}

private void setupDefaultAssistantPref(final PackageManager pm) {
    // todo: whatever sneaky nonsense the g assistant uses to highlight system settings should also be used here
    final Preference systemDefaultAssistant = findPreference("default_assistant");
    if (systemDefaultAssistant == null) return;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        final Intent assistantSettings = new Intent(Settings.ACTION_VOICE_INPUT_SETTINGS);
        if (assistantSettings.resolveActivity(pm) != null) {
            systemDefaultAssistant.setIntent(assistantSettings);
            return;
        }
    }
    systemDefaultAssistant.setVisible(false);
}

private void setupNotificationsPref(final PackageManager pm) {
    final Preference appNotifications = requireNonNull(findPreference("notifications"));
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        final Intent notifSettings = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, Apps.PKG);
        if (notifSettings.resolveActivity(pm) != null) {
            appNotifications.setIntent(notifSettings);
            return;
        }
    }
    appNotifications.setVisible(false);
}

private void setupAccessibilityButtonPref(final PackageManager pm) {
    final Preference accessibilityButton = requireNonNull(findPreference("accessibility_button"));
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        final String showArgs = Apps.PKG + '/' + EmillaAccessibilityService.class.getName();
        final Bundle bundle = new Bundle();
        bundle.putString(EXTRA_FRAGMENT_ARG_KEY, showArgs);
        final Intent in = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, Apps.PKG)
                .putExtra(EXTRA_FRAGMENT_ARG_KEY, showArgs)
                .putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle);
        if (in.resolveActivity(pm) != null) {
            accessibilityButton.setIntent(in);
            return;
        }
    }
    accessibilityButton.setVisible(false);
}

private void setupAppInfoPref(final PackageManager pm) {
    final Preference systemAppInfo = requireNonNull(findPreference("app_info"));
    final Intent in = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + Apps.PKG));
    if (in.resolveActivity(pm) != null) systemAppInfo.setIntent(in);
    else systemAppInfo.setVisible(false);
}
}