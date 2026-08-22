package net.emilla.command.app;

import net.emilla.annotation.internal;
import net.emilla.wadget.ActionSurface;

enum Discord {;
    public static final String PKG = "com.discord";

    @internal static AppSend instance(ActionSurface surface, AppEntry appEntry) {
        return new AppSend(surface, appEntry);
    }
}
