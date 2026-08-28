package net.emilla.command.app;

import net.emilla.annotation.internal;
import net.emilla.command.CommandYielder;
import net.emilla.wadget.ActionSurface;

public final class AppYielder extends CommandYielder {
    private final AppEntry mApp;

    @internal AppYielder(AppEntry app) {
        mApp = app;
    }

    @Override
    public boolean usesInstruction() {
        return mApp.actions.usesInstruction();
    }

    @Override
    protected AppCommand makeCommand(ActionSurface surface) {
        KnownApp known = mApp.known;
        if (known != null) {
            return known.make(surface, mApp);
        }

        return mApp.actions.usesInstruction()
            ? new AppInstruct(surface, mApp)
            : new AppCommand(surface, mApp)
        ;
    }
}
