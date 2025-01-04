package net.emilla.command.core;

import android.content.Intent;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Apps;
import net.emilla.utils.Dialogs;

public class OpenLaunch extends OpenCommand {

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_launch;
    }

    public OpenLaunch(AssistActivity act, String instruct) {
        super(act, instruct, R.string.command_launch, R.string.instruction_app);
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
