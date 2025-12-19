package net.emilla.command.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.RequiresApi;

import net.emilla.annotation.internal;
import net.emilla.command.app.AppEntry;
import net.emilla.util.Apps;
import net.emilla.util.Intents;

final class Notifications extends OpenCommand {

    public static boolean possible(PackageManager pm) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && Apps.canDo(pm, new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS));
    }

    @internal Notifications(Context ctx) {
        super(ctx, CoreEntry.NOTIFICATIONS, EditorInfo.IME_ACTION_GO);
    }

    @Override @RequiresApi(Build.VERSION_CODES.O)
    protected Intent defaultIntent() {
        return Intents.notificationSettings();
    }

    @Override @RequiresApi(Build.VERSION_CODES.O)
    protected Intent makeIntent(AppEntry app, PackageManager pm) {
        return Intents.notificationSettings(app.pkg);
    }

}