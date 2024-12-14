package net.emilla.chime;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;

import net.emilla.settings.SettingVals;

public class Custom implements Chimer {
    private static Uri uriOf(SharedPreferences prefs, String prefString) {
        String uriStr = prefs.getString(prefString, null);
        return uriStr != null ? Uri.parse(uriStr) : null;
    }

    private final Context mContext;
    private final Uri[] mUris = new Uri[7];

    public Custom(Context ctx, SharedPreferences prefs) {
        mContext = ctx;

        mUris[Chimer.START] = uriOf(prefs, SettingVals.CHIME_START);
        mUris[Chimer.ACT] = uriOf(prefs, SettingVals.CHIME_ACT);
        mUris[Chimer.PEND] = uriOf(prefs, SettingVals.CHIME_PEND);
        mUris[Chimer.RESUME] = uriOf(prefs, SettingVals.CHIME_RESUME);
        mUris[Chimer.EXIT] = uriOf(prefs, SettingVals.CHIME_EXIT);
        mUris[Chimer.SUCCEED] = uriOf(prefs, SettingVals.CHIME_SUCCEED);
        mUris[Chimer.FAIL] = uriOf(prefs, SettingVals.CHIME_FAIL);
    }

    @Override
    public void chime(byte id) {
        MediaPlayer player = MediaPlayer.create(mContext, mUris[id]);
        if (player == null) player = MediaPlayer.create(mContext, Nebula.sound(id));
        // If the URI is null or broken fall back to nebula
        player.setOnCompletionListener(MediaPlayer::release);
        player.start();
    }
}
