package net.emilla.command.app;

import net.emilla.activity.AssistActivity;

/*internal*/ final class AospContacts {

    public static final String PKG = "com.android.contacts";

    /*internal*/ static AppSearch instance(AssistActivity act, AppEntry appEntry) {
        return new AppSearch(act, appEntry);
    }

    private AospContacts() {}

}
