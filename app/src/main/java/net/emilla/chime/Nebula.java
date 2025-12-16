package net.emilla.chime;

import android.content.Context;
import android.media.MediaPlayer;

/// The default chimer, Nebula.
final class Nebula implements Chimer {

    /*internal*/ Nebula() {}

    @Override
    public void chime(Context ctx, Chime chime) {
        MediaPlayer player = mediaPlayer(ctx, chime);
        player.setOnCompletionListener(MediaPlayer::release);
        player.start();
    }

    public static MediaPlayer mediaPlayer(Context ctx, Chime chime) {
        return MediaPlayer.create(ctx, chime.nebulaSound);
    }

}
