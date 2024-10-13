package net.emilla.system;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import net.emilla.R;
import net.emilla.config.ConfigActivity;
import net.emilla.utils.Apps;

public class EmillaForegroundService extends Service {
    private static final String CHANNEL_ID = "foreground_service_channel";
    private static boolean sRunning = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (sRunning) return START_STICKY;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_assistant) // TODO: un-break the icons
                .setContentTitle(getString(R.string.notif_foreground_title))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        getString(R.string.channel_foreground),
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(getString(R.string.dscript_foreground));
                nm.createNotificationChannel(channel);
            }
            builder.setContentText(getString(R.string.notif_foreground_text)); // "tap to disable" text
            Intent notifIntent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, Apps.PKG)
                    .putExtra(Settings.EXTRA_CHANNEL_ID, CHANNEL_ID);
            notifIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent notifPend = PendingIntent.getActivity(this, 0, notifIntent, PendingIntent.FLAG_IMMUTABLE);
            builder.setContentIntent(notifPend);
        }
        Intent configIntent = Apps.meTask(this, ConfigActivity.class); // TODO: actually take to the settings fragment with notbroken navigation state
        PendingIntent configPend = PendingIntent.getActivity(this, 0, configIntent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Action serviceConfigAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_settings, // TODO: un-break the icons
                getString(R.string.notif_action_foreground_config),
                configPend).build();
        builder.addAction(serviceConfigAction);
        startForeground(1, builder.build());
        sRunning = true;
        return START_STICKY;
    }
}
