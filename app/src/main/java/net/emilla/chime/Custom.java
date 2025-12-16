package net.emilla.chime;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;

import net.emilla.config.SettingVals;

final class Custom implements Chimer {

    private final Uri[] mUris;

    /*internal*/ Custom(SharedPreferences prefs) {
        Chime[] chimes = Chime.values();
        int chimeCount = chimes.length;
        var uris = new Uri[chimeCount];

        for (int i = 0; i < chimeCount; ++i) {
            uris[i] = SettingVals.customChimeSoundUri(prefs, chimes[i]);
        }

        mUris = uris;
    }

    @Override
    public void chime(Context ctx, Chime chime) {
        Uri uri = mUris[chime.ordinal()];

        MediaPlayer player;
        if (uri != null) {
            player = MediaPlayer.create(ctx, uri);
            if (player == null) {
                // URI is broken
                player = Nebula.mediaPlayer(ctx, chime);
            }
        } else {
            player = Nebula.mediaPlayer(ctx, chime);
        }

        player.setOnCompletionListener(MediaPlayer::release);
        player.start();
    }

}
