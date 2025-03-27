package net.emilla.event;

import static net.emilla.BuildConfig.DEBUG;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.emilla.ping.PingIntent;
import net.emilla.ping.Pinger;
import net.emilla.util.Permissions;

public final class PingReceiver extends BroadcastReceiver {

    private static final String TAG = PingReceiver.class.getSimpleName();

    @Override @SuppressLint("MissingPermission")
    public void onReceive(Context ctx, Intent intent) {
        if (Permissions.pings(ctx)) Pinger.of(ctx, new PingIntent(intent)).ping();
        else if (DEBUG) Log.e(TAG, "Unable to ping due to lack of permission.");
    }
}
