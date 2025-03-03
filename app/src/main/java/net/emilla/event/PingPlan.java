package net.emilla.event;

import android.app.Notification;

import androidx.annotation.NonNull;

public final class PingPlan extends Plan {

    public final Notification ping;
    public final String channel;

    public PingPlan(int slot, long time, @NonNull Notification ping, @NonNull String channel) {
        super(slot, time);

        this.ping = ping;
        this.channel = channel;
    }

    public static PingPlan afterSeconds(
        int slot,
        int seconds,
        @NonNull Notification ping,
        @NonNull String channel
    ) {
        return new PingPlan(slot, System.currentTimeMillis() + seconds * 1000L, ping, channel);
    }
}
