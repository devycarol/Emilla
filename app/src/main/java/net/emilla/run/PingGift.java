package net.emilla.run;

import android.Manifest;
import android.app.Notification;

import androidx.annotation.RequiresPermission;

import net.emilla.activity.AssistActivity;
import net.emilla.ping.PingChannel;
import net.emilla.ping.Pinger;

public final class PingGift implements CommandRun {

    private final Notification mPing;
    private final PingChannel mChannel;

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public PingGift(Notification ping, PingChannel channel) {
        mPing = ping;
        mChannel = channel;
    }

    @Override @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public void run(AssistActivity act) {
        Pinger.of(act, mPing, mChannel).ping();
    }
}
