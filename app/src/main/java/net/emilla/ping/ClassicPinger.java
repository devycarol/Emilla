package net.emilla.ping;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.RequiresPermission;

import net.emilla.util.Services;

/*internal open*/ class ClassicPinger implements Pinger {

    private static int sSlot = 0;

    private static synchronized int uniqueSlot() {
        return --sSlot;
    }

    protected final NotificationManager pingManager;
    private final Notification mPing;
    private final int mSlot;

    public ClassicPinger(Context ctx, Notification ping, PingChannel channel) {
        pingManager = Services.notification(ctx);
        mPing = ping;
        mSlot = channel.slot;
    }

    @Override @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public /*open*/ void ping() {
        int id = mSlot == PingChannel.SLOT_UNLIMITED ? uniqueSlot() : mSlot;
        // this can be used to edit or remove the notification later.
        pingManager.notify(id, mPing);
    }
}
