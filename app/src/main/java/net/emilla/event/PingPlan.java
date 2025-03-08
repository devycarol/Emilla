package net.emilla.event;

import android.app.Notification;

public final class PingPlan extends Plan {

    public final Notification ping;
    public final String channel;

    public PingPlan(int slot, long time, Notification ping, String channel) {
        super(slot, time);

        this.ping = ping;
        this.channel = channel;
    }

    public static PingPlan afterSeconds(int slot, int seconds, Notification ping, String channel) {
        return new PingPlan(slot, System.currentTimeMillis() + seconds * 1000L, ping, channel);
    }
}
