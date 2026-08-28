package net.emilla.command.app;

import android.content.Intent;

import net.emilla.Feedback;
import net.emilla.annotation.internal;
import net.emilla.annotation.open;
import net.emilla.command.EmillaCommand;
import net.emilla.util.Intents;
import net.emilla.wadget.ActionSurface;

public @open class AppCommand extends EmillaCommand {
    protected final AppEntry appEntry;

    @internal AppCommand(ActionSurface surface, AppEntry appEntry) {
        super(surface, appEntry);

        this.appEntry = appEntry;
    }

    protected final Feedback send(String message) {
        return Feedback.succeed(Intents.sendToApp(this.appEntry.pkg)
            .putExtra(Intent.EXTRA_TEXT, message)
        );
    }

    @Override
    protected @open Feedback run(ActionSurface surface) {
        return Feedback.succeed(this.appEntry.launchIntent());
    }

    @Override
    protected @open Feedback run(ActionSurface surface, String ignored) {
        return run(surface); // Todo: remove this from the interface for non-instructables.
    }
}
