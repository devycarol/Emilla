package net.emilla.event;

import android.app.Notification;

import net.emilla.ping.PingChannel;

public final class PingPlan extends Plan {

    public final Notification ping;
    public final String channel;

    private PingPlan(int slot, long time, Notification ping, String channel) {
        super(slot, time);

        this.ping = ping;
        this.channel = channel;
    }

    public static PingPlan afterSeconds(
        int slot,
        int seconds,
        Notification ping,
        PingChannel channel
    ) {
        return new PingPlan(slot, System.currentTimeMillis() + seconds * 1000L, ping, channel.id);
    }
}
