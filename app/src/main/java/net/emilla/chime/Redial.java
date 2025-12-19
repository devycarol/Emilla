package net.emilla.chime;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;

import net.emilla.annotation.internal;

final class Redial implements Chimer {

    private final ToneGenerator mToneGenerator = new ToneGenerator(
        AudioManager.STREAM_MUSIC,
        ToneGenerator.MAX_VOLUME
    );

    @internal Redial() {}

    @Override
    public void chime(Context ctx, Chime chime) {
        mToneGenerator.startTone(chime.redialTone);
    }

}
