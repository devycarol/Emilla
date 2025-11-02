package net.emilla.command.app;

import net.emilla.activity.AssistActivity;

/*internal*/ final class Firefox {

    public static final String PKG = "org.mozilla.firefox";

    /*internal*/ static AppSearch instance(AssistActivity act, AppEntry appEntry) {
        return new AppSearch(act, appEntry);
    }

    private Firefox() {}

}
