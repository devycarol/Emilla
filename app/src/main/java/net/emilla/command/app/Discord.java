package net.emilla.command.app;

import android.content.Context;

/*internal*/ final class Discord {

    public static final String PKG = "com.discord";

    /*internal*/ static AppSend instance(Context ctx, AppEntry appEntry) {
        return new AppSend(ctx, appEntry);
    }

    private Discord() {}

}
