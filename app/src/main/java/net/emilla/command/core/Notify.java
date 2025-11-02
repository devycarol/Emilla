package net.emilla.command.core;

import android.Manifest;
import android.annotation.SuppressLint;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.ping.PingChannel;
import net.emilla.ping.Pings;
import net.emilla.util.Permissions;

/*internal*/ final class Notify extends CoreDataCommand {

    public static final String ENTRY = "notify";

    public static boolean possible() {
        return true;
    }

    /*internal*/ Notify(AssistActivity act) {
        super(act, CoreEntry.NOTIFY, R.string.data_hint_notify);
    }

    @Override
    protected void run() {
        tryPing(str(R.string.ping_command), null);
    }

    @Override
    protected void run(String title) {
        tryPing(title, null);
    }

    @Override
    protected void runWithData(String text) {
        tryPing(str(R.string.ping_command), text);
    }

    @Override
    protected void runWithData(String title, String text) {
        tryPing(title, text);
    }

    @SuppressLint("MissingPermission")
    private void tryPing(String title, @Nullable String text) {
        Permissions.withPings(this.activity, () -> ping(title, text));
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void ping(String title, @Nullable String text) {
        givePing(
            Pings.make(
                this.activity,
                PingChannel.COMMAND,
                title, text,

                R.drawable.ic_notify
            ),
            PingChannel.command()
        );
    }

}
