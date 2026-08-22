package net.emilla.command.app;

import net.emilla.annotation.internal;
import net.emilla.wadget.ActionSurface;

enum Signal {;
    public static final String PKG = "org.thoughtcrime.securesms";

    @internal static MultilineMessenger instance(ActionSurface surface, AppEntry appEntry) {
        return new MultilineMessenger(surface, appEntry);
    }
}
