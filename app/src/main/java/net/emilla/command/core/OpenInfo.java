package net.emilla.command.core;

import android.content.Intent;
import android.provider.Settings;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Apps;
import net.emilla.utils.Dialogs;

public class OpenInfo extends OpenCommand {

    public static final String ENTRY = "info";

    private static class InfoParams extends CoreParams {

        private InfoParams() {
            super(R.string.command_info,
                  R.string.instruction_app,
                  R.drawable.ic_info,
                  EditorInfo.IME_ACTION_GO);
        }
    }

    public OpenInfo(AssistActivity act, String instruct) {
        super(act, instruct, new InfoParams());
    }

    @Override
    protected AlertDialog.Builder getAppChooser(AssistActivity act) {
        return Dialogs.appChooser(act, act.getPackageManager(), mAppList);
    }

    @Override
    protected Intent makeIntent(String pkg, String cls) {
        return new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Apps.pkgUri(pkg));
    }

    @Override
    protected void run() {
        // Todo: it may be useful to include listings beyond those in the launcher icons, or be able to
        //  search by package name.
        appSucceed(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Apps.pkgUri(Apps.MY_PKG)));
    }
}
