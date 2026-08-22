package net.emilla.command.app;

import net.emilla.annotation.internal;
import net.emilla.wadget.ActionSurface;

enum Firefox {;
    public static final String PKG = "org.mozilla.firefox";

    @internal static AppSearch instance(ActionSurface surface, AppEntry appEntry) {
        return new AppSearch(surface, appEntry);
    }
}
