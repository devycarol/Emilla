package net.emilla.command.app;

import net.emilla.activity.AssistActivity;

/*internal*/ final class NewPipe {

    public static final String PKG = "org.schabi.newpipe";

    /*internal*/ static VideoSearchBySend instance(AssistActivity act, AppEntry appEntry) {
        return new VideoSearchBySend(act, appEntry);
    }

    private NewPipe() {}

}
