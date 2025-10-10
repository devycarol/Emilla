package net.emilla.ping;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresPermission;

import net.emilla.util.Services;

public sealed class Pinger permits ChanneledPinger {

    public static Pinger of(Context ctx, PingIntent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new ChanneledPinger(ctx, intent.ping(), intent.channel());
        }
        return new Pinger(ctx, intent.ping(), intent.channel());
    }

    public static Pinger of(Context ctx, Notification ping, PingChannel channel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new ChanneledPinger(ctx, ping, channel);
        }
        return new Pinger(ctx, ping, channel);
    }

    private static int sSlot = 0;

    private static synchronized int uniqueSlot() {
        return --sSlot;
    }

    protected final NotificationManager pPingManager;
    private final Notification mPing;
    private final int mSlot;

    /*internal*/ Pinger(Context ctx, Notification ping, PingChannel channel) {
        pPingManager = Services.notification(ctx);
        mPing = ping;
        mSlot = channel.slot;
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public /*open*/ void ping() {
        int id = mSlot == PingChannel.SLOT_UNLIMITED ? uniqueSlot() : mSlot;
        // this can be used to edit or remove the notification later.
        pPingManager.notify(id, mPing);
    }
}
