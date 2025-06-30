package net.emilla.command.core;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.app.Apps;

public final class Notifications extends OpenCommand {

    public static final String ENTRY = "notifications";
    @StringRes
    public static final int NAME = R.string.command_notifications;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_notifications;

    public static Yielder yielder() {
        return new Yielder(true, Notifications::new, ENTRY, NAME, ALIASES);
    }

    public static boolean possible(PackageManager pm) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && Apps.canDo(pm, new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS));
    }

    private Notifications(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_app,
              R.drawable.ic_notifications,
              R.string.summary_notifications,
              R.string.manual_notifications,
              EditorInfo.IME_ACTION_GO);
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