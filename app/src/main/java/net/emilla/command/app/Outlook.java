package net.emilla.command.app;

import net.emilla.R;
import net.emilla.annotation.internal;
import net.emilla.wadget.ActionSurface;

enum Outlook {;
    public static final String PKG = "com.microsoft.office.outlook";

    @internal static AppSendData instance(
        ActionSurface surface,
        AppEntry appEntry
    ) {
        return new AppSendData(surface, appEntry, R.string.data_hint_email);
    }
}
