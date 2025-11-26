package net.emilla.command.core;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import net.emilla.activity.AssistActivity;
import net.emilla.util.Apps;
import net.emilla.util.Intents;

/*internal*/ final class Notifications extends OpenCommand {

    public static boolean possible(PackageManager pm) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && Apps.canDo(pm, new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS));
    }

    /*internal*/ Notifications(AssistActivity act) {
        super(act, CoreEntry.NOTIFICATIONS, EditorInfo.IME_ACTION_GO);
    }

    @Override @RequiresApi(Build.VERSION_CODES.O)
    protected void run(AssistActivity act) {
        appSucceed(act, Intents.notificationSettings());
    }

    @Override @RequiresApi(Build.VERSION_CODES.O)
    protected void run(AssistActivity act, String app) {
        appSearchRun(act, app, appEntry -> Intents.notificationSettings(appEntry.pkg));
    }

    @Override
    protected AlertDialog.Builder makeChooser(AssistActivity act) {
        // TODO: this isn't needed.
        return null;
    }

}