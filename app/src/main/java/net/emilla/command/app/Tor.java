package net.emilla.command.app;

import net.emilla.annotation.internal;
import net.emilla.wadget.ActionSurface;

enum Tor {;
    public static final String PKG = "org.torproject.torbrowser";

    @internal static AppCommand instance(ActionSurface surface, AppEntry appEntry) {
        return new AppCommand(surface, appEntry);
    }
}
