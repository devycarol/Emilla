package net.emilla.run;

import android.Manifest;
import android.app.Notification;

import androidx.annotation.RequiresPermission;

import net.emilla.ping.PingChannel;
import net.emilla.ping.Pinger;

public enum PingGift {;
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public static CommandRun instance(Notification ping, PingChannel channel) {
        return act -> {
            Pinger.of(act, ping, channel).ping();
        };
    }
}
