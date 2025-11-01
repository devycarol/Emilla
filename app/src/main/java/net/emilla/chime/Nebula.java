package net.emilla.chime;

import android.content.Context;
import android.media.MediaPlayer;

/// The default chimer, Nebula.
/*internal*/ final class Nebula implements Chimer {

    /*internal*/ Nebula() {}

    @Override
    public void chime(Context ctx, Chime chime) {
        var player = MediaPlayer.create(ctx, chime.nebulaSound);
        player.setOnCompletionListener(MediaPlayer::release);
        player.start();
    }

}
