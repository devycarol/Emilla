package net.emilla.command.core;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.datafield.DataDirective;
import net.emilla.datafield.DataField;
import net.emilla.ping.PingChannel;
import net.emilla.ping.Pinger;
import net.emilla.ping.Pings;
import net.emilla.util.Permission;
import net.emilla.wadget.ActionSurface;

final class Notify extends EmillaCommand {
    @internal Notify(ActionSurface surface) {
        super(surface, CoreEntry.NOTIFY);
    }

    @Override
    public DataDirective dataDirective() {
        return new DataField(R.string.data_hint_notify);
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        var res = surface.getResources();
        return run(surface, res.getString(R.string.ping_command));
    }

    @Override
    protected Feedback run(ActionSurface surface, String title) {
        return tryPing(surface.getAssistActivity(), title, surface.dataText());
    }

    @SuppressLint("MissingPermission")
    private static Feedback tryPing(
        AssistActivity act,
        CharSequence title,
        @Nullable CharSequence text
    ) {
        Permission.PINGS.with(act, () -> act.take(ping(act, title, text)));
        return Feedback.silence();
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private static Feedback ping(
        Context ctx,
        CharSequence title,
        @Nullable CharSequence text
    ) {
        Notification ping = Pings.make(
            ctx,
            PingChannel.COMMAND,
            title,
            text,
            R.drawable.ic_notify
        );
        Pinger.of(ctx, ping, PingChannel.COMMAND).ping();
        return Feedback.give();
    }
}
