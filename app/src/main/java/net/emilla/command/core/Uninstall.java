package net.emilla.command.core;

import static android.content.Intent.ACTION_UNINSTALL_PACKAGE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AlertDialog;

import net.emilla.activity.AssistActivity;
import net.emilla.util.Apps;
import net.emilla.util.Dialogs;
import net.emilla.util.Intents;

/*internal*/ final class Uninstall extends OpenCommand {

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, new Intent(ACTION_UNINSTALL_PACKAGE, Apps.packageUri("")))
            // todo: ACTION_UNINSTALL_PACKAGE is deprecated?
            || Apps.canDo(pm, Intents.appInfo(""))
            || Apps.canDo(pm, new Intent(Settings.ACTION_SETTINGS));
    }

    /*internal*/ Uninstall(AssistActivity act) {
        super(act, CoreEntry.UNINSTALL, EditorInfo.IME_ACTION_GO);
    }

    @Override
    protected void run(AssistActivity act) {
        offerDialog(act, this.appChooser);
    }

    @Override
    protected void run(AssistActivity act, String app) {
        appSearchRun(act, app, appEntry -> Intents.uninstallApp(appEntry.pkg, act.getPackageManager()));
    }

    @Override
    protected AlertDialog.Builder makeChooser(AssistActivity act) {
        return Dialogs.appUninstalls(act);
    }

}
