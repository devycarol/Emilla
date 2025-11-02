package net.emilla.command.app;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

/*internal*/ final class Markor {

    public static final String PKG = "net.gsantner.markor";
    public static final String CLS_MAIN = PKG + ".activity.MainActivity";

    /*internal*/ static AppSendData instance(AssistActivity act, AppEntry appEntry) {
        return new AppSendData(act, appEntry, R.string.data_hint_note);
    }

    private Markor() {}

}
