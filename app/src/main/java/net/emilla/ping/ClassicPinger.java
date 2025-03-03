package net.emilla.ping;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.RequiresPermission;

/*open*/ class ClassicPinger implements Pinger {

    protected final NotificationManager pingManager;
    private final Notification mPing;
    private final int mSlot;

    /**
     * Singleton that decrements for each notification posted using
     * {@link PingChannel#SLOT_UNLIMITED}
     */
    private static int sSlot = 0;

    public ClassicPinger(Context ctx, Notification ping, PingChannel channel) {
        pingManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        mPing = ping;
        mSlot = channel.slot;
    }

    @Override @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public /*open*/ void ping() {
        int id = mSlot == PingChannel.SLOT_UNLIMITED ? --sSlot : mSlot;
        // this can be used to edit or remove the notification later.
        pingManager.notify(id, mPing);
    }
}
