package net.emilla.chime

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import net.emilla.R

/**
 * The default Nebula chimer.
 *
 * @param ctx it's important to use application context to avoid memory leaks!
 */
class Nebula(private val ctx: Context) : Chimer {

    override fun chime(id: Byte) {
        // TODO: still encountering occasional sound cracking issues
        val player: MediaPlayer = MediaPlayer.create(ctx, sound(id))
        player.setOnCompletionListener(MediaPlayer::release)
        player.start()
    }

    companion object {
        @JvmStatic @RawRes
        internal fun sound(chime: Byte) = when (chime) {
            Chimer.START -> R.raw.nebula_start
            Chimer.ACT -> R.raw.nebula_act
            Chimer.PEND -> R.raw.nebula_pend
            Chimer.RESUME -> R.raw.nebula_resume
            Chimer.EXIT -> R.raw.nebula_exit
            Chimer.SUCCEED -> R.raw.nebula_succeed
            Chimer.FAIL -> R.raw.nebula_fail
            else -> 0
        }
    }
}
