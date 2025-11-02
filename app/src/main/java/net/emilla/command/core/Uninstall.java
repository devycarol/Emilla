package net.emilla.command.core;

import static android.content.Intent.ACTION_UNINSTALL_PACKAGE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AlertDialog;

import net.emilla.activity.AssistActivity;
import net.emilla.apps.Apps;
import net.emilla.util.Dialogs;

/*internal*/ final class Uninstall extends OpenCommand {

    public static final String ENTRY = "uninstall";

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, new Intent(ACTION_UNINSTALL_PACKAGE, Apps.pkgUri("")))
            // todo: ACTION_UNINSTALL_PACKAGE is deprecated?
            || Apps.canDo(pm, Apps.infoTask(""))
            || Apps.canDo(pm, new Intent(Settings.ACTION_SETTINGS));
    }

    /*internal*/ Uninstall(AssistActivity act) {
        super(act, CoreEntry.UNINSTALL, EditorInfo.IME_ACTION_GO);
    }

    @Override
    protected void run() {
        offerDialog(this.appChooser);
    }

    @Override
    protected void run(String app) {
        appSearchRun(app, appEntry -> Apps.uninstallIntent(appEntry.pkg, pm()));
    }

    @Override
    protected AlertDialog.Builder makeChooser() {
        return Dialogs.appUninstalls(this.activity);
    }

}
