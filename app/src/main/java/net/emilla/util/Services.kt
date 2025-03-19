@file:JvmName("Services")

package net.emilla.util

import android.content.Context
import android.media.AudioManager

fun audio(ctx: Context): AudioManager {
    return ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
}