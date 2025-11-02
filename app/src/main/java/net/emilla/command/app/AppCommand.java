package net.emilla.command.app;

import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.command.EmillaCommand;

public /*open*/ class AppCommand extends EmillaCommand {

    @FunctionalInterface
    public interface Maker {

        AppCommand make(AssistActivity act, AppEntry appEntry);

    }

    protected final AppEntry appEntry;

    /*internal*/ AppCommand(AssistActivity act, AppEntry appEntry) {
        this(act, appEntry, EditorInfo.IME_ACTION_GO);
    }

    /*internal*/ AppCommand(AssistActivity act, AppEntry appEntry, int imeAction) {
        super(act, appEntry, imeAction);

        this.appEntry = appEntry;
    }

    @Override
    public final boolean shouldLowercase() {
        return false; // App names shouldn't be lowercased.
    }

    @Override @Deprecated
    protected final String dupeLabel() {
        return this.appEntry.label + " (" + this.appEntry.pkg + ')';
    }

    @Override
    public final boolean usesAppIcon() {
        return true;
    }

    @Override
    protected final void run() {
        appSucceed(this.appEntry.launchIntent());
    }

    @Override
    protected /*open*/ void run(String ignored) {
        run(); // Todo: remove this from the interface for non-instructables.
    }

}
