package net.emilla.command.app;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

/*internal*/ final class Outlook {

    public static final String PKG = "com.microsoft.office.outlook";

    /*internal*/ static AppSendData instance(AssistActivity act, AppEntry appEntry) {
        return new AppSendData(act, appEntry, R.string.data_hint_email);
    }

    private Outlook() {}

}
