package net.emilla.command.core;

import static android.content.Intent.ACTION_UNINSTALL_PACKAGE;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;

import net.emilla.command.app.AppEntry;
import net.emilla.util.Apps;
import net.emilla.util.Intents;

/*internal*/ final class Uninstall extends OpenCommand {

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, new Intent(ACTION_UNINSTALL_PACKAGE, Apps.packageUri("")))
            // todo: ACTION_UNINSTALL_PACKAGE is deprecated?
            || Apps.canDo(pm, Intents.appInfo(""))
            || Apps.canDo(pm, new Intent(Settings.ACTION_SETTINGS));
    }

    /*internal*/ Uninstall(Context ctx) {
        super(ctx, CoreEntry.UNINSTALL, EditorInfo.IME_ACTION_GO);
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
