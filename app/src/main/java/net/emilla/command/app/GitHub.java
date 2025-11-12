package net.emilla.command.app;

import android.content.Context;

import net.emilla.R;

/*internal*/ final class GitHub  {

    public static final String PKG = "com.github.android";

    /*internal*/ static AppSendData instance(Context ctx, AppEntry appEntry) {
        return new AppSendData(ctx, appEntry, R.string.data_hint_issue);
    }

    private GitHub() {}

}
