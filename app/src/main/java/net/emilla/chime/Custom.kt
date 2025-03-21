package net.emilla.chime

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.net.Uri

/**
 * A custom-sound chimer built from user settings.
 *
 * @param ctx it's important to use application context to avoid memory leaks!
 * @param prefs used to fetch sound URIs from user settings.
 */
class Custom(private val ctx: Context, prefs: SharedPreferences) : Chimer {
    private val uris: Array<Uri?> = Chimer.customSounds(prefs)

    override fun chime(id: Byte) {
        var player = MediaPlayer.create(ctx, uris[id.toInt()])
            ?: MediaPlayer.create(ctx, Nebula.sound(id))
        // fall back to nebula URI is broken or null
        player.setOnCompletionListener(MediaPlayer::release)
        player.start()
    }
}
