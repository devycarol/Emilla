package net.emilla.command.app;

import android.content.Context;

final class YouTube {

    public static final String PKG = "com.google.android.youtube";

    /*internal*/ static AppSearch instance(Context ctx, AppEntry appEntry) {
        return new AppSearch(ctx, appEntry);
        // Todo: instantly pull up bookmarked videos, specialized search for channels, playlists,
        //  etc. I assume the G assistant has similar functionality. If requires internet could use
        //  bookmarks at the very least. Also, this command is broken when a video is playing.
    }

    private YouTube() {}

}
