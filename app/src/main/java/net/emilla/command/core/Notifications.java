package net.emilla.command.core;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import net.emilla.activity.AssistActivity;
import net.emilla.apps.Apps;

/*internal*/ final class Notifications extends OpenCommand {

    public static final String ENTRY = "notifications";

    public static boolean possible(PackageManager pm) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && Apps.canDo(pm, new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS));
    }

    /*internal*/ Notifications(AssistActivity act) {
        super(act, CoreEntry.NOTIFICATIONS, EditorInfo.IME_ACTION_GO);
    }

    @Override @RequiresApi(api = Build.VERSION_CODES.O)
    protected void run() {
        appSucceed(Apps.notificationsTask());
    }

    @Override @RequiresApi(api = Build.VERSION_CODES.O)
    protected void run(String app) {
        appSearchRun(app, appEntry -> Apps.notificationsTask(appEntry.pkg));
    }

    @Override
    protected AlertDialog.Builder makeChooser() {
        // TODO: this isn't needed.
        return null;
    }

}