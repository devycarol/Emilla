package net.emilla.command.app;

import net.emilla.activity.AssistActivity;

/*internal*/ final class YouTube {

    public static final String PKG = "com.google.android.youtube";

    /*internal*/ static AppSearch instance(AssistActivity act, AppEntry appEntry) {
        return new AppSearch(act, appEntry);
        // Todo: instantly pull up bookmarked videos, specialized search for channels, playlists,
        //  etc. I assume the G assistant has similar functionality. If requires internet could use
        //  bookmarks at the very least. Also, this command is broken when a video is playing.
    }

    private YouTube() {}

}
