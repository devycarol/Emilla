package net.emilla.command.app;

import net.emilla.annotation.internal;
import net.emilla.wadget.ActionSurface;

enum AospContacts {;
    public static final String PKG = "com.android.contacts";

    @internal static AppSearch instance(ActionSurface surface, AppEntry appEntry) {
        return new AppSearch(surface, appEntry);
    }
}
