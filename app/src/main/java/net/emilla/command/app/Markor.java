package net.emilla.command.app;

import android.content.Context;

import net.emilla.R;

final class Markor {

    public static final String PKG = "net.gsantner.markor";
    public static final String CLS_MAIN = PKG + ".activity.MainActivity";

    /*internal*/ static AppSendData instance(Context ctx, AppEntry appEntry) {
        return new AppSendData(ctx, appEntry, R.string.data_hint_text);
    }

    private Markor() {}

}
