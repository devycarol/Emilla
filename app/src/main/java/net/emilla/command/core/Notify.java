package net.emilla.command.core;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.ping.PingChannel;
import net.emilla.ping.Pings;
import net.emilla.util.Permission;

/*internal*/ final class Notify extends CoreDataCommand {

    /*internal*/ Notify(Context ctx) {
        super(ctx, CoreEntry.NOTIFY, R.string.data_hint_notify);
    }

    @Override
    protected void run(AssistActivity act) {
        var res = act.getResources();
        tryPing(act, res.getString(R.string.ping_command), null);
    }

    @Override
    protected void run(AssistActivity act, String title) {
        tryPing(act, title, null);
    }

    @Override
    public void runWithData(AssistActivity act, String text) {
        var res = act.getResources();
        tryPing(act, res.getString(R.string.ping_command), text);
    }

    @Override
    public void runWithData(AssistActivity act, String title, String text) {
        tryPing(act, title, text);
    }

    @SuppressLint("MissingPermission")
    private static void tryPing(AssistActivity act, String title, @Nullable String text) {
        Permission.PINGS.with(act, () -> ping(act, title, text));
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private static void ping(AssistActivity act, String title, @Nullable String text) {
        givePing(
            act,
            Pings.make(
                act, PingChannel.COMMAND,

                title, text,

                R.drawable.ic_notify
            ),
            PingChannel.command()
        );
    }

}
