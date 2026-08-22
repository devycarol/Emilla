package net.emilla.command.app;

import android.content.Context;
import android.view.inputmethod.EditorInfo;

import net.emilla.Feedback;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.annotation.open;
import net.emilla.command.EmillaCommand;
import net.emilla.wadget.ActionSurface;

public @open class AppCommand extends EmillaCommand {
    @FunctionalInterface
    public interface Maker {
        AppCommand make(Context ctx, AppEntry appEntry);
    }

    protected final AppEntry appEntry;

    @internal AppCommand(Context ctx, AppEntry appEntry) {
        this(ctx, appEntry, EditorInfo.IME_ACTION_GO);
    }

    @internal AppCommand(Context ctx, AppEntry appEntry, int imeAction) {
        super(ctx, appEntry, imeAction);

        this.appEntry = appEntry;
    }

    @Override
    protected final Feedback run(ActionSurface surface, AssistActivity act) {
        return Feedback.succeed(this.appEntry.launchIntent());
    }

    @Override
    protected @open Feedback run(ActionSurface surface, AssistActivity act, String ignored) {
        return run(surface, act); // Todo: remove this from the interface for non-instructables.
    }
}
