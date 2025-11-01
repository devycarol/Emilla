package net.emilla.apps;

import android.content.pm.PackageManager;

import net.emilla.command.app.Tasker;

public final class AppActions {

    public static final int FLAG_SEND    = 0x1;
    public static final int FLAG_SEARCH  = 0x2;
    public static final int FLAG_SPECIAL = 0x4;

    public static int of(PackageManager pm, String pkg, int mask) {
        if (pkg.equals(Tasker.PKG)) return FLAG_SPECIAL;

        int flags = 0;

        if ((mask & FLAG_SEND) != 0 && Apps.canDo(pm, Apps.sendToApp(pkg))) {
            flags |= FLAG_SEND;
        }

        if ((mask & FLAG_SEARCH) != 0 && Apps.canDo(pm, Apps.searchToApp(pkg))) {
            flags |= FLAG_SEARCH;
        }

        return flags;
    }

    private AppActions() {}
}
