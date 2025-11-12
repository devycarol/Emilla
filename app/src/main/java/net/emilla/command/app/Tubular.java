package net.emilla.command.app;

import android.content.Context;

/*internal*/ final class Tubular {

    public static final String PKG = "org.polymorphicshade.tubular";

    /*internal*/ static VideoSearchBySend instance(Context ctx, AppEntry appEntry) {
        return new VideoSearchBySend(ctx, appEntry);
    }

    private Tubular() {}

}
