package net.emilla.command.core;

import android.Manifest;
import android.annotation.SuppressLint;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.command.DataField;
import net.emilla.command.EmillaCommand;
import net.emilla.ping.PingChannel;
import net.emilla.ping.Pings;
import net.emilla.util.Permission;
import net.emilla.wadget.ActionSurface;

final class Notify extends EmillaCommand {
    @internal Notify(ActionSurface surface) {
        super(
            surface,
            CoreEntry.NOTIFY,
            new DataField(R.string.data_hint_notify)
        );
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
