package net.emilla.command.app;

import android.content.Context;

final class Signal {

    public static final String PKG = "org.thoughtcrime.securesms";

    /*internal*/ static MultilineMessenger instance(Context ctx, AppEntry appEntry) {
        return new MultilineMessenger(ctx, appEntry);
    }

    private Signal() {}

}
