package net.emilla.command.app;

import net.emilla.Feedback;
import net.emilla.annotation.internal;
import net.emilla.annotation.open;
import net.emilla.command.EmillaCommand;
import net.emilla.command.ImeAction;
import net.emilla.wadget.ActionSurface;

public @open class AppCommand extends EmillaCommand {
    @FunctionalInterface
    public interface Maker {
        AppCommand make(ActionSurface surface, AppEntry appEntry);
    }

    protected final AppEntry appEntry;

    @internal AppCommand(ActionSurface surface, AppEntry appEntry) {
        this(surface, appEntry, ImeAction.GO);
    }

    @internal AppCommand(
        ActionSurface surface,
        AppEntry appEntry,
        ImeAction imeAction
    ) {
        super(surface, appEntry, imeAction);

        this.appEntry = appEntry;
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
