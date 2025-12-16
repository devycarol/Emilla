package net.emilla.command.app;

import android.content.Context;

final class NewPipe {

    public static final String PKG = "org.schabi.newpipe";

    /*internal*/ static VideoSearchBySend instance(Context ctx, AppEntry appEntry) {
        return new VideoSearchBySend(ctx, appEntry);
    }

    private NewPipe() {}

}
