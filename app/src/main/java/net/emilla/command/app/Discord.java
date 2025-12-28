package net.emilla.command.app;

import android.content.Context;

import net.emilla.annotation.internal;

enum Discord {
    ;

    public static final String PKG = "com.discord";

    @internal static AppSend instance(Context ctx, AppEntry appEntry) {
        return new AppSend(ctx, appEntry);
    }

}
