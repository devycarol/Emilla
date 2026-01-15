package net.emilla.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import net.emilla.R;
import net.emilla.action.QuickAction;
import net.emilla.chime.Chime;
import net.emilla.chime.Chimer;
import net.emilla.command.core.CoreEntry;
import net.emilla.lang.Lang;
import net.emilla.result.ChimeSoundResult;
import net.emilla.result.GetChimeSound;
import net.emilla.util.Features;

public final class BehaviorSettingsFragment extends PreferenceFragmentCompat {
    private final ActivityResultLauncher<Chime> mSoundPickerLauncher = registerForActivityResult(
        new GetChimeSound(),
        this::onPickChimeSound
    );

    private boolean mUsingCustomSounds;

    public BehaviorSettingsFragment() {
        super();
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.settings_behavior, rootKey);

        var manager = getPreferenceManager();
        var prefs = manager.getSharedPreferences();
        mUsingCustomSounds = SettingVals.chimerId(prefs).equals(Chimer.CUSTOM);

        var ctx = requireContext();
        var res = ctx.getResources();
        setupDefaultCommandPref(res);

        var pm = ctx.getPackageManager();
        if (!Features.torch(pm)) {
            removeTorch(findPreference(QuickAction.PREF_NO_COMMAND));
            removeTorch(findPreference(QuickAction.PREF_DOUBLE_ASSIST));
            removeTorch(findPreference(QuickAction.PREF_LONG_SUBMIT));
            removeTorch(findPreference(QuickAction.PREF_MENU_KEY));
        }

        setupChimerPref();
        setupCustomSoundPrefs(ctx, prefs, res, mUsingCustomSounds);
    }

    private void setupDefaultCommandPref(Resources res) {
        ListPreference defaultCommand = findPreference(SettingVals.DEFAULT_COMMAND);
        defaultCommand.setEntries(CoreEntry.entryNames(res));
        defaultCommand.setEntryValues(CoreEntry.entryValues());
        // todo: allow apps
    }

    private static void removeTorch(ListPreference actionPref) {
        // todo: more robustly manage quick actions
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

    private void setupChimerPref() {
        findPreference(SettingVals.CHIMER).setOnPreferenceChangeListener(
            (pref, newVal) -> {
                boolean usingCustomSounds = newVal.equals(Chimer.CUSTOM);

                if (mUsingCustomSounds != usingCustomSounds) {
                    var ctx = requireContext();
                    var res = ctx.getResources();
                    var manager = getPreferenceManager();
                    var prefs = manager.getSharedPreferences();
                    for (var chime : Chime.values()) {
                        Preference customSound = findPreference(chime.preferenceKey);
                        customSound.setVisible(usingCustomSounds);
                        activateCustomSoundPref(ctx, prefs, res, customSound, chime);
                    }
                    mUsingCustomSounds = usingCustomSounds;
                }

                return true;
            }
        );
    }

    private void setupCustomSoundPrefs(
        Context ctx,
        SharedPreferences prefs,
        Resources res,
        boolean isEnabled
    ) {
        for (var chime : Chime.values()) {
            Preference customSound = findPreference(chime.preferenceKey);
            if (isEnabled) {
                activateCustomSoundPref(ctx, prefs, res, customSound, chime);
            } else {
                customSound.setVisible(false);
            }
        }
    }

    private void activateCustomSoundPref(
        Context ctx,
        SharedPreferences prefs,
        Resources res,
        Preference customSound,
        Chime chime
    ) {
        customSound.setOnPreferenceClickListener(pref -> {
            mSoundPickerLauncher.launch(chime);
            return false;
        });

        customSound.setSummary(customSoundTitle(ctx, prefs, res, chime));
    }

    private void onPickChimeSound(ChimeSoundResult chimeSoundResult) {
        if (chimeSoundResult == null) {
            return;
        }

        var manager = getPreferenceManager();
        var prefs = manager.getSharedPreferences();

        Chime chime = chimeSoundResult.chime();
        Uri soundUri = chimeSoundResult.soundUri();
        if (soundUri != null) {
            SettingVals.setCustomSound(prefs, chime, soundUri);
        } else {
            SettingVals.deleteCustomChimeSound(prefs, chime);
        }

        var ctx = requireContext();
        var res = ctx.getResources();
        Preference customSound = findPreference(chime.preferenceKey);
        customSound.setSummary(customSoundTitle(ctx, prefs, res, chime));
    }

    private static String customSoundTitle(
        Context ctx,
        SharedPreferences prefs,
        Resources res,
        Chime chime
    ) {
        Uri soundUri = SettingVals.customChimeSoundUri(prefs, chime);

        if (soundUri != null) {
            Ringtone ringtone = RingtoneManager.getRingtone(ctx, soundUri);
            if (ringtone != null) {
                return ringtone.getTitle(ctx);
            }
        }

        return Lang.wordConcat(res, R.string.sound_set_nebula, chime.name);
    }
}
