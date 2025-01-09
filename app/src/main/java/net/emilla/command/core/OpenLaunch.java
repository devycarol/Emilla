package net.emilla.command.core;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Apps;
import net.emilla.utils.Dialogs;

public class OpenLaunch extends OpenCommand {

    public static final String ENTRY = "launch";

    private static class LaunchParams extends CoreParams {

        private LaunchParams() {
            super(R.string.command_launch,
                  R.string.instruction_app,
                  R.drawable.ic_launch,
                  EditorInfo.IME_ACTION_GO);
        }
    }

    public OpenLaunch(AssistActivity act, String instruct) {
        super(act, instruct, new LaunchParams());
    }

    @Override
    protected AlertDialog.Builder getAppChooser(AssistActivity act) {
        return Dialogs.appChooser(act, act.getPackageManager(), mAppList);
    }

    @Override
    protected Intent makeIntent(String pkg, String cls) {
        return Apps.launchIntent(pkg, cls);
    }

    @Override
    protected void run() {
        offerDialog(mAppChooser);
    }
}
