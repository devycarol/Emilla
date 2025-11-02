package net.emilla.command.app;

import net.emilla.activity.AssistActivity;

/*internal*/ final class Discord {

    public static final String PKG = "com.discord";

    /*internal*/ static AppSend instance(AssistActivity act, AppEntry appEntry) {
        return new AppSend(act, appEntry);
    }

    private Discord() {}

}
