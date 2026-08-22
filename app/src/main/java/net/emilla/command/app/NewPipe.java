package net.emilla.command.app;

import net.emilla.annotation.internal;
import net.emilla.wadget.ActionSurface;

enum NewPipe {;
    public static final String PKG = "org.schabi.newpipe";

    @internal static VideoSearchBySend instance(ActionSurface surface, AppEntry appEntry) {
        return new VideoSearchBySend(surface, appEntry);
    }
}
