package net.emilla.command.app;

import net.emilla.annotation.internal;
import net.emilla.wadget.ActionSurface;

enum Tubular {;
    public static final String PKG = "org.polymorphicshade.tubular";

    @internal static VideoSearchBySend instance(ActionSurface surface, AppEntry appEntry) {
        return new VideoSearchBySend(surface, appEntry);
    }
}
