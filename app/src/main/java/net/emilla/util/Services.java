package net.emilla.util;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.view.accessibility.AccessibilityManager;

public enum Services {
    ;

    public static AccessibilityManager accessibility(Context ctx) {
        return (AccessibilityManager) ctx.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    public static AlarmManager alarm(Context ctx) {
        return (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
    }

    public static AudioManager audio(Context ctx) {
        return (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
    }

    public static CameraManager camera(Context ctx) {
        return (CameraManager) ctx.getSystemService(Context.CAMERA_SERVICE);
    }

    public static ClipboardManager clipboard(Context ctx) {
        return (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    public static NotificationManager notification(Context ctx) {
        return (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
    }

}
