package net.emilla.chime;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.annotation.Nullable;

public final class Custom implements Chimer {

    @Nullable
    private static Uri uriOf(SharedPreferences prefs, String prefString) {
        var uriStr = prefs.getString(prefString, null);
        return uriStr != null ? Uri.parse(uriStr) : null;
    }

    private final Context mContext;
    private final Uri[] mUris = new Uri[7];

    public Custom(Context ctx, SharedPreferences prefs) {
        mContext = ctx;

        mUris[Chimer.START] = uriOf(prefs, Chimer.PREF_START);
        mUris[Chimer.ACT] = uriOf(prefs, Chimer.PREF_ACT);
        mUris[Chimer.PEND] = uriOf(prefs, Chimer.PREF_PEND);
        mUris[Chimer.RESUME] = uriOf(prefs, Chimer.PREF_RESUME);
        mUris[Chimer.EXIT] = uriOf(prefs, Chimer.PREF_EXIT);
        mUris[Chimer.SUCCEED] = uriOf(prefs, Chimer.PREF_SUCCEED);
        mUris[Chimer.FAIL] = uriOf(prefs, Chimer.PREF_FAIL);
    }

    @Override
    public void chime(byte id) {
        var player = MediaPlayer.create(mContext, mUris[id]);
        if (player == null) player = MediaPlayer.create(mContext, Nebula.sound(id));
        // If the URI is null or broken fall back to nebula
        player.setOnCompletionListener(MediaPlayer::release);
        player.start();
    }
}
