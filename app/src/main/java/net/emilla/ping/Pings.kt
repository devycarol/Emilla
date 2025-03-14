@file:JvmName("Pings")

package net.emilla.ping

import android.app.Notification
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat

/**
 * Constructs a notification with given parameters.
 *
 * @param ctx used to build the notification.
 * @param channel notification channel.
 * @param title notification title.
 * @param text notification text.
 * @param icon small icon for the notification.
 * @param sticky whether the notification should be non-dismissible. *This is no longer supported
 *               from Android 14, consider it advisory.*
 * @return a notification builder with the specified parameters.
 */
@JvmOverloads
fun make(
    ctx: Context,
    channel: String,
    @StringRes title: Int,
    @StringRes text: Int,
    @DrawableRes icon: Int,
    sticky: Boolean = false
): Notification {
    val res = ctx.resources
    return make(ctx, channel, res.getString(title), res.getString(text), icon, sticky)
}

/**
 * Constructs a notification with given parameters.
 *
 * @param ctx used to build the notification.
 * @param channel notification channel.
 * @param title notification title.
 * @param text notification text.
 * @param icon small icon for the notification.
 * @param sticky whether the notification should be non-dismissible. *This is no longer supported
 *               from Android 14, consider it advisory.*
 * @return a notification builder with the specified parameters.
 */
@JvmOverloads
fun make(
    ctx: Context,
    channel: String,
    title: CharSequence,
    text: CharSequence?,
    @DrawableRes icon: Int,
    sticky: Boolean = false
): Notification {
    return NotificationCompat.Builder(ctx, channel).apply {
        setContentTitle(title)
        if (text != null) setContentText(text)
        setSmallIcon(icon)
        if (sticky) setOngoing(true)
    }.build()
}
