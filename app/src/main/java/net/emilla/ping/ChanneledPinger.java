package net.emilla.ping;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;

@RequiresApi(Build.VERSION_CODES.O)
final class ChanneledPinger extends Pinger {

    private final PingChannel mChannel;
    private final Resources mRes;

    /*internal*/ ChanneledPinger(Context ctx, Notification ping, PingChannel channel) {
        super(ctx, ping, channel);

        mChannel = channel;
        mRes = ctx.getResources();
    }

    @Override @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public void ping() {
        if (this.pingManager.getNotificationChannel(mChannel.id) == null) {
            this.pingManager.createNotificationChannel(mChannel.make(mRes));
            // TODO LANG: you need to update the channel name & description when language changes.
            // todo: more centralized channel ensurement so there's consistent order in the app
            //  notifications settings.
        }
        super.ping();
    }
}
