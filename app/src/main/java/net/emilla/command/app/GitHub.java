package net.emilla.command.app;

import net.emilla.R;
import net.emilla.annotation.internal;
import net.emilla.wadget.ActionSurface;

enum GitHub {;
    public static final String PKG = "com.github.android";

    @internal static AppSendData instance(ActionSurface surface, AppEntry appEntry) {
        return new AppSendData(surface, appEntry, R.string.data_hint_issue);
    }
}
