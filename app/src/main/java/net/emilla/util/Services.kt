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
fun Context.accessibilityService() = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
@JvmName("alarm")
fun Context.alarmService() = getSystemService(Context.ALARM_SERVICE) as AlarmManager
@JvmName("audio")
fun Context.audioService() = getSystemService(Context.AUDIO_SERVICE) as AudioManager
@JvmName("camera")
fun Context.cameraService() = getSystemService(Context.CAMERA_SERVICE) as CameraManager
@JvmName("clipboard")
fun Context.clipboardService() = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
@JvmName("notification")
fun Context.notificationService() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
