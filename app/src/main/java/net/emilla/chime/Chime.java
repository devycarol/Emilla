package net.emilla.chime;

import android.media.ToneGenerator;

import androidx.annotation.RawRes;

import net.emilla.R;

public enum Chime {
    START(R.raw.nebula_start, ToneGenerator.TONE_PROP_BEEP, "chime_start"),
    ACT(R.raw.nebula_act, ToneGenerator.TONE_PROP_PROMPT, "chime_act"),
    PEND(R.raw.nebula_pend, ToneGenerator.TONE_PROP_BEEP, "chime_pend"),
    RESUME(R.raw.nebula_resume, ToneGenerator.TONE_PROP_BEEP, "chime_resume"),
    EXIT(R.raw.nebula_exit, ToneGenerator.TONE_PROP_BEEP2, "chime_exit"),
    SUCCEED(R.raw.nebula_succeed, ToneGenerator.TONE_PROP_ACK, "chime_succeed"),
    FAIL(R.raw.nebula_fail, ToneGenerator.TONE_PROP_NACK, "chime_fail");

    @RawRes
    public final int nebulaSound;
    public final int redialTone;
    public final String preferenceKey;

    Chime(@RawRes int nebulaSound, int redialTone, String preferenceKey) {
        this.nebulaSound = nebulaSound;
        this.redialTone = redialTone;
        this.preferenceKey = preferenceKey;
    }

}
