package net.emilla.command.app;

import net.emilla.R;
import net.emilla.annotation.internal;
import net.emilla.wadget.ActionSurface;

enum Markor {;
    public static final String PKG = "net.gsantner.markor";
    public static final String CLS_MAIN = PKG + ".activity.MainActivity";

    @internal static AppSendData instance(
        ActionSurface surface,
        AppEntry appEntry
    ) {
        return new AppSendData(surface, appEntry, R.string.data_hint_text);
    }
}
