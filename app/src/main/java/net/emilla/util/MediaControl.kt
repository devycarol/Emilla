@file:JvmName("MediaControl")

package net.emilla.util

import android.media.AudioManager
import android.view.KeyEvent

fun AudioManager.sendPlayEvent() = sendButtonEvent(KeyEvent.KEYCODE_MEDIA_PLAY)
fun AudioManager.sendPauseEvent() = sendButtonEvent(KeyEvent.KEYCODE_MEDIA_PAUSE)
fun AudioManager.sendPlayPauseEvent() = sendButtonEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)

private fun AudioManager.sendButtonEvent(keyCode: Int) {
    dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
    dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
}
