@file:JvmName("Services")

package net.emilla.util

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.ClipboardManager
import android.content.Context
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.view.accessibility.AccessibilityManager

@JvmName("accessibility")
fun Context.accessibilityService(): AccessibilityManager {
    return getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
}

@JvmName("alarm")
fun Context.alarmService(): AlarmManager {
    return getSystemService(Context.ALARM_SERVICE) as AlarmManager
}

@JvmName("audio")
fun Context.audioService(): AudioManager {
    return getSystemService(Context.AUDIO_SERVICE) as AudioManager
}

@JvmName("camera")
fun Context.cameraService(): CameraManager {
    return getSystemService(Context.CAMERA_SERVICE) as CameraManager
}

@JvmName("clipboard")
fun Context.clipboardService(): ClipboardManager {
    return getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
}

@JvmName("notification")
fun Context.notificationService(): NotificationManager {
    return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}
