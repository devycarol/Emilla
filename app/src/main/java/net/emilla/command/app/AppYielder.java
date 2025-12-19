package net.emilla.command.app;

import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.command.CommandYielder;

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
    protected AppCommand makeCommand(AssistActivity act) {
        AppProperties properties = mApp.properties;
        if (properties != null) {
            return properties.maker.make(act, mApp);
        }

        return mApp.actions.defaultCommand(act, mApp);
    }

}
