package net.emilla.command.core;

import static android.content.Intent.ACTION_UNINSTALL_PACKAGE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;

import androidx.annotation.Nullable;

import net.emilla.annotation.internal;
import net.emilla.command.app.AppEntry;
import net.emilla.util.Apps;
import net.emilla.util.Intents;
import net.emilla.wadget.ActionSurface;

final class Uninstall extends OpenCommand {
    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, new Intent(ACTION_UNINSTALL_PACKAGE, Apps.packageUri("")))
            // todo: ACTION_UNINSTALL_PACKAGE is deprecated?
            || Apps.canDo(pm, Intents.appInfo(""))
            || Apps.canDo(pm, new Intent(Settings.ACTION_SETTINGS));
    }

    @internal Uninstall(ActionSurface surface) {
        super(surface, CoreEntry.UNINSTALL);
    }

    @Override @Nullable
    protected Intent defaultIntent() {
        return null;
    }

    @Override
    protected Intent makeIntent(AppEntry app, PackageManager pm) {
        return Intents.uninstallApp(app.pkg, pm);
    }
}
