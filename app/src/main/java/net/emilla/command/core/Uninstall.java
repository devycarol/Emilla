package net.emilla.command.core;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.settings.Aliases;
import net.emilla.utils.Apps;
import net.emilla.utils.Dialogs;

public class Uninstall extends OpenCommand {

    public static final String ENTRY = "uninstall";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_uninstall;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    private static class UninstallParams extends CoreParams {

        private UninstallParams() {
            super(R.string.command_uninstall,
                  R.string.instruction_app,
                  R.drawable.ic_uninstall,
                  EditorInfo.IME_ACTION_GO);
        }
    }

    public Uninstall(AssistActivity act, String instruct) {
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
