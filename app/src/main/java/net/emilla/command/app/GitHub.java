package net.emilla.command.app;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

/*internal*/ final class GitHub  {

    public static final String PKG = "com.github.android";

    /*internal*/ static AppSendData instance(AssistActivity act, AppEntry appEntry) {
        return new AppSendData(act, appEntry, R.string.data_hint_issue);
    }

    private GitHub() {}

}
