package net.emilla.run;

import android.Manifest;
import android.app.Notification;
import android.content.Context;

import androidx.annotation.RequiresPermission;

import net.emilla.ping.PingChannel;
import net.emilla.ping.Pinger;

public final class PingGift implements Runnable {

    private final Context mContext;
    private final Notification mPing;
    private final PingChannel mChannel;

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public PingGift(Context ctx, Notification ping, PingChannel channel) {
        mContext = ctx;
        mPing = ping;
        mChannel = channel;
    }

    @Override @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public void run() {
        Pinger.of(mContext, mPing, mChannel).ping();
    }
}
