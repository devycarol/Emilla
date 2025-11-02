package net.emilla.command.app;

import net.emilla.activity.AssistActivity;

/*internal*/ final class Signal {

    public static final String PKG = "org.thoughtcrime.securesms";

    /*internal*/ static MultilineMessenger instance(AssistActivity act, AppEntry appEntry) {
        return new MultilineMessenger(act, appEntry);
    }

    private Signal() {}

}
