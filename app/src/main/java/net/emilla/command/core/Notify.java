package net.emilla.command.core;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.ping.PingChannel;
import net.emilla.ping.Pings;
import net.emilla.util.Permission;
import net.emilla.wadget.ActionSurface;

final class Notify extends CoreDataCommand {
    @internal Notify(Context ctx) {
        super(ctx, CoreEntry.NOTIFY, R.string.data_hint_notify);
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        var act = surface.getAssistActivity();
        var res = surface.getResources();
        return tryPing(act, res.getString(R.string.ping_command), null);
    }

    @Override
    protected Feedback run(ActionSurface surface, String title) {
        return tryPing(surface.getAssistActivity(), title, null);
    }

    @Override
    public Feedback runWithData(ActionSurface surface, String text) {
        var act = surface.getAssistActivity();
        var res = surface.getResources();
        return tryPing(act, res.getString(R.string.ping_command), text);
    }

    @Override
    public Feedback runWithData(
        ActionSurface surface,
        String title,
        String text
    ) {
        return tryPing(surface.getAssistActivity(), title, text);
    }

    @SuppressLint("MissingPermission")
    private static Feedback tryPing(
        AssistActivity act,
        String title,
        @Nullable String text
    ) {
        Permission.PINGS.with(act, () -> ping(act, title, text));
        return Feedback.silence();
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private static void ping(
        AssistActivity act,
        String title,
        @Nullable String text
    ) {
        givePing(
            act,
            Pings.make(
                act,
                PingChannel.COMMAND,
                title,
                text,
                R.drawable.ic_notify
            ),
            PingChannel.COMMAND
        );
    }
}
