package net.emilla.command.core;

import android.content.pm.PackageManager;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AlertDialog;

import net.emilla.activity.AssistActivity;
import net.emilla.apps.Apps;

/*internal*/ final class Info extends OpenCommand {

    public static final String ENTRY = "info";

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, Apps.infoTask(""));
    }

    /*internal*/ Info(AssistActivity act) {
        super(act, CoreEntry.INFO, EditorInfo.IME_ACTION_GO);
    }

    @Override
    protected void run() {
        // Todo: it may be useful to include listings beyond those in the launcher icons, or be able to
        //  search by package name.
        appSucceed(Apps.infoTask());
    }

    @Override
    protected void run(String app) {
        appSearchRun(app, appEntry -> Apps.infoTask(appEntry.pkg));
    }

    @Override
    protected AlertDialog.Builder makeChooser() {
        // TODO: this isn't needed
        return null;
    }

}
