package net.emilla.command.app;

import android.content.Context;

import net.emilla.annotation.internal;

enum Tor {
    ;

    public static final String PKG = "org.torproject.torbrowser";

    @internal static AppCommand instance(Context ctx, AppEntry appEntry) {
        return new AppCommand(ctx, appEntry);
    }

}
