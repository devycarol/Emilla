package net.emilla.ping;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresPermission;

public interface Pinger {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    void ping();

    static Pinger of(Context ctx, PingIntent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new ModernPinger(ctx, intent.ping(), intent.channel());
        } return new ClassicPinger(ctx, intent.ping(), intent.channel());
    }

    static Pinger of(Context ctx, Notification ping, PingChannel channel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new ModernPinger(ctx, ping, channel);
        } return new ClassicPinger(ctx, ping, channel);
    }
}
