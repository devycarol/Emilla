package net.emilla.command.app;

import net.emilla.activity.AssistActivity;

/*internal*/ final class Tor {

    public static final String PKG = "org.torproject.torbrowser";

    /*internal*/ static AppCommand instance(AssistActivity act, AppEntry appEntry) {
        return new AppCommand(act, appEntry);
    }

    private Tor() {}

}
