package net.emilla.command.core;

import android.content.pm.PackageManager;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AlertDialog;

import net.emilla.activity.AssistActivity;
import net.emilla.util.Apps;
import net.emilla.util.Intents;

/*internal*/ final class Info extends OpenCommand {

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, Intents.appInfo(""));
    }

    /*internal*/ Info(AssistActivity act) {
        super(act, CoreEntry.INFO, EditorInfo.IME_ACTION_GO);
    }

    @Override
    protected void run(AssistActivity act) {
        // Todo: it may be useful to include listings beyond those in the launcher icons, or be able to
        //  search by package name.
        appSucceed(act, Intents.appInfo());
    }

    @Override
    protected void run(AssistActivity act, String app) {
        appSearchRun(act, app, appEntry -> Intents.appInfo(appEntry.pkg));
    }

    @Override
    protected AlertDialog.Builder makeChooser(AssistActivity act) {
        // TODO: this isn't needed
        return null;
    }

}
