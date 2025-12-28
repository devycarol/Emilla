package net.emilla.command.app;

import android.content.Context;

import net.emilla.R;
import net.emilla.annotation.internal;

enum GitHub  {
    ;

    public static final String PKG = "com.github.android";

    @internal static AppSendData instance(Context ctx, AppEntry appEntry) {
        return new AppSendData(ctx, appEntry, R.string.data_hint_issue);
    }

}
