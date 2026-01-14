package net.emilla.ping;

import android.app.Notification;
import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public enum Pings {
    ;
    public static Notification make(
        Context ctx,
        PingChannel channel,
        CharSequence title,
        @Nullable CharSequence text,
        @DrawableRes int icon
    ) {
        var builder = new NotificationCompat.Builder(ctx, channel.id)
            .setContentTitle(title)
            .setSmallIcon(icon);

        if (text != null) {
            builder.setContentText(text);
        }

        return builder.build();
    }
}
