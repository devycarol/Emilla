package net.emilla.command.core;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Apps;
import net.emilla.utils.Dialogs;

public class OpenUninstall extends OpenCommand {

    public static final String ENTRY = "uninstall";

    private static class UninstallParams extends CoreParams {

        private UninstallParams() {
            super(R.string.command_uninstall,
                  R.string.instruction_app,
                  R.drawable.ic_uninstall,
                  EditorInfo.IME_ACTION_GO);
        }
    }

    public OpenUninstall(AssistActivity act, String instruct) {
        super(act, instruct, new UninstallParams());
    }

    @Override
    protected AlertDialog.Builder getAppChooser(AssistActivity act) {
        return Dialogs.appUninstaller(act, mAppList);
    }

    @Override
    protected Intent makeIntent(String pkg, String cls) {
        return Apps.uninstallIntent(pkg, pm());
    }

    @Override
    protected void run() {
        offerDialog(mAppChooser);
    }
}
