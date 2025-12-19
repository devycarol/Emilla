package net.emilla.command.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.inputmethod.EditorInfo;

import net.emilla.annotation.internal;
import net.emilla.command.app.AppEntry;
import net.emilla.util.Apps;
import net.emilla.util.Intents;

final class Info extends OpenCommand {

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, Intents.appInfo(""));
    }

    @internal Info(Context ctx) {
        super(ctx, CoreEntry.INFO, EditorInfo.IME_ACTION_GO);
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
