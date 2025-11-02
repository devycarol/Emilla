package net.emilla.result;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import net.emilla.chime.Chime;
import net.emilla.config.SettingVals;

public final class GetChimeSound extends ActivityResultContract<Chime, ChimeSoundResult> {

    @Nullable
    private Chime mRaceCondition = null;

    public GetChimeSound() {}

    @Override
    public Intent createIntent(Context ctx, Chime chime) {
        mRaceCondition = chime;

        var res = ctx.getResources();

        var getSound = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
            .putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false)
            .putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
            .putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
            .putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, res.getString(chime.name));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        Uri uri = SettingVals.customChimeSoundUri(prefs, chime);
        if (uri != null) {
            getSound.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);
        }

        return getSound;
    }

    @Override @Nullable
    public ChimeSoundResult parseResult(int resultCode, @Nullable Intent data) {
        if (mRaceCondition == null) {
            return null;
        }

        if (resultCode != Activity.RESULT_OK || data == null) {
            mRaceCondition = null;
            return null;
        }

        Uri soundUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
        var result = new ChimeSoundResult(mRaceCondition, soundUri);

        mRaceCondition = null;

        return result;
    }

}
