package net.emilla.ping;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import net.emilla.event.PingReceiver;

public final class PingIntent extends Intent {

    private static final String
            EXTRA_PING = "ping",
            EXTRA_CHANNEL = "channel";

    public PingIntent(Intent intent) {
        super(intent);
    }

    public PingIntent(Context ctx, Notification ping, String channel) {
        super(ctx, PingReceiver.class);

        putExtra(EXTRA_PING, ping);
        putExtra(EXTRA_CHANNEL, channel);
    }

    public Notification ping() {
        return getParcelableExtra(EXTRA_PING);
    }

    public PingChannel channel() {
        return PingChannel.of(getStringExtra(EXTRA_CHANNEL));
    }
}
