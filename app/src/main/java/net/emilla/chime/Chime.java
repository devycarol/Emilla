package net.emilla.chime;

import android.media.ToneGenerator;

import androidx.annotation.RawRes;
import androidx.annotation.StringRes;

import net.emilla.R;

public enum Chime {
    START("chime_start", R.string.chime_start, R.raw.nebula_start, ToneGenerator.TONE_PROP_BEEP),
    ACT("chime_act", R.string.chime_act, R.raw.nebula_act, ToneGenerator.TONE_PROP_PROMPT),
    PEND("chime_pend", R.string.chime_pend, R.raw.nebula_pend, ToneGenerator.TONE_PROP_BEEP),
    RESUME("chime_resume", R.string.chime_resume, R.raw.nebula_resume, ToneGenerator.TONE_PROP_BEEP),
    EXIT("chime_exit", R.string.chime_exit, R.raw.nebula_exit, ToneGenerator.TONE_PROP_BEEP2),
    SUCCEED("chime_succeed", R.string.chime_succeed, R.raw.nebula_succeed, ToneGenerator.TONE_PROP_ACK),
    FAIL("chime_fail", R.string.chime_fail, R.raw.nebula_fail, ToneGenerator.TONE_PROP_NACK);

    public final String preferenceKey;
    @StringRes
    public final int name;
    @RawRes
    public final int nebulaSound;
    public final int redialTone;

    Chime(String preferenceKey, @StringRes int name, @RawRes int nebulaSound, int redialTone) {
        this.name = name;
        this.nebulaSound = nebulaSound;
        this.redialTone = redialTone;
        this.preferenceKey = preferenceKey;
    }

}
