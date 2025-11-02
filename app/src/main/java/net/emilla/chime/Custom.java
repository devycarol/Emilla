package net.emilla.chime;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;

/*internal*/ final class Custom implements Chimer {

    private final Uri[] mUris;

    /*internal*/ Custom(SharedPreferences prefs) {
        Chime[] chimes = Chime.values();
        int chimeCount = chimes.length;
        var uris = new Uri[chimeCount];

        for (int i = 0; i < chimeCount; ++i) {
            String soundUri = prefs.getString(chimes[i].preferenceKey, null);
            if (soundUri != null) {
                uris[i] = Uri.parse(soundUri);
            }
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
                player = MediaPlayer.create(ctx, chime.nebulaSound);
            }
        } else {
            player = MediaPlayer.create(ctx, chime.nebulaSound);
        }

        player.setOnCompletionListener(MediaPlayer::release);
        player.start();
    }

}
