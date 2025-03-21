package net.emilla.chime

import android.media.AudioManager
import android.media.ToneGenerator

class Redial : Chimer {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME)

    override fun chime(id: Byte) {
        toneGenerator.startTone(tone(id))
    }

    private fun tone(id: Byte) = when (id) {
        Chimer.START, Chimer.PEND, Chimer.RESUME -> ToneGenerator.TONE_PROP_BEEP
        Chimer.ACT -> ToneGenerator.TONE_PROP_PROMPT
        Chimer.EXIT -> ToneGenerator.TONE_PROP_BEEP2
        Chimer.SUCCEED -> ToneGenerator.TONE_PROP_ACK
        Chimer.FAIL -> ToneGenerator.TONE_PROP_NACK
        else -> -1
    }
}
