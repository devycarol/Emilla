package net.emilla.command.app;

import android.content.Context;

import net.emilla.annotation.internal;

enum AospContacts {
    ;

    public static final String PKG = "com.android.contacts";

    @internal static AppSearch instance(Context ctx, AppEntry appEntry) {
        return new AppSearch(ctx, appEntry);
    }

}
