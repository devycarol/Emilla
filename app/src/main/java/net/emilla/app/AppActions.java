package net.emilla.app;

import static net.emilla.app.Apps.searchToApp;
import static net.emilla.app.Apps.sendToApp;

import android.content.pm.PackageManager;

import net.emilla.command.app.Tasker;

public final class AppActions {

    public static final int FLAG_SEND    = 0x1,
                            FLAG_SEARCH  = 0x2,
                            FLAG_SPECIAL = 0x4;

    public static int of(PackageManager pm, String pkg, int mask) {
        if (pkg.equals(Tasker.PKG)) return FLAG_SPECIAL;

        int flags = 0;

        if (((mask & FLAG_SEND) != 0)
        &&  Apps.canDo(pm, sendToApp(pkg))) flags |= FLAG_SEND;

        if (((mask & FLAG_SEARCH) != 0)
        &&  Apps.canDo(pm, searchToApp(pkg))) flags |= FLAG_SEARCH;

        return flags;
    }

    private AppActions() {}
}
