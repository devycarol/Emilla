package net.emilla.command.app;

import net.emilla.Feedback;
import net.emilla.annotation.internal;
import net.emilla.annotation.open;
import net.emilla.wadget.ActionSurface;

@open class AppSend extends AppCommand {
    @internal AppSend(ActionSurface surface, AppEntry appEntry) {
        super(surface, appEntry);
    }

    @Override
    protected @open Feedback run(ActionSurface surface, String message) {
        return send(message);
    }
}
