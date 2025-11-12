package net.emilla.command.app;

import android.content.Context;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.command.EmillaCommand;

public /*open*/ class AppCommand extends EmillaCommand {

    @FunctionalInterface
    public interface Maker {

        AppCommand make(Context ctx, AppEntry appEntry);

    }

    protected final AppEntry appEntry;

    /*internal*/ AppCommand(Context ctx, AppEntry appEntry) {
        this(ctx, appEntry, EditorInfo.IME_ACTION_GO);
    }

    /*internal*/ AppCommand(Context ctx, AppEntry appEntry, int imeAction) {
        super(ctx, appEntry, imeAction);

        this.appEntry = appEntry;
    }

    @Override
    protected final void run(AssistActivity act) {
        appSucceed(act, this.appEntry.launchIntent());
    }

    @Override
    protected /*open*/ void run(AssistActivity act, String ignored) {
        run(act); // Todo: remove this from the interface for non-instructables.
    }

}
