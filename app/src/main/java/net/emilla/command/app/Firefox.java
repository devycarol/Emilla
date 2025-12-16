package net.emilla.command.app;

import android.content.Context;

final class Firefox {

    public static final String PKG = "org.mozilla.firefox";

    /*internal*/ static AppSearch instance(Context ctx, AppEntry appEntry) {
        return new AppSearch(ctx, appEntry);
    }

    private Firefox() {}

}
