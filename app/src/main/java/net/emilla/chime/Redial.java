package net.emilla.chime;

import android.media.AudioManager;
import android.media.ToneGenerator;

public final class Redial implements Chimer {
    private final ToneGenerator mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);

    @Override
    public void chime(byte id) {
        mToneGenerator.startTone(switch (id) {
            case Chimer.START, Chimer.PEND, Chimer.RESUME -> ToneGenerator.TONE_PROP_BEEP;
            case Chimer.ACT -> ToneGenerator.TONE_PROP_PROMPT;
            case Chimer.EXIT -> ToneGenerator.TONE_PROP_BEEP2;
            case Chimer.SUCCEED -> ToneGenerator.TONE_PROP_ACK;
            case Chimer.FAIL -> ToneGenerator.TONE_PROP_NACK;
            default -> -1;
        });
    }
}
