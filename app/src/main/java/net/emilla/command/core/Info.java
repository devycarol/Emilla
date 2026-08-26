package net.emilla.command.core;

import android.content.Intent;
import android.content.pm.PackageManager;

import net.emilla.annotation.internal;
import net.emilla.command.app.AppEntry;
import net.emilla.util.Apps;
import net.emilla.util.Intents;
import net.emilla.wadget.ActionSurface;

final class Info extends OpenCommand {
    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, Intents.appInfo(""));
    }

    @internal Info(ActionSurface surface) {
        super(surface, CoreEntry.INFO);
    }

    @Override
    protected Intent defaultIntent() {
        return Intents.appInfo();
    }

    @Override
    public Intent makeIntent(AppEntry app, PackageManager pm) {
        return Intents.appInfo(app.pkg);
    }
}
