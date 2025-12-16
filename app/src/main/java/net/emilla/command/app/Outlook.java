package net.emilla.command.app;

import android.content.Context;

import net.emilla.R;

final class Outlook {

    public static final String PKG = "com.microsoft.office.outlook";

    /*internal*/ static AppSendData instance(Context ctx, AppEntry appEntry) {
        return new AppSendData(ctx, appEntry, R.string.data_hint_email);
    }

    private Outlook() {}

}
