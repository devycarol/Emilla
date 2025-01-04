package net.emilla.command.core;

import android.content.Intent;
import android.provider.Settings;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Apps;
import net.emilla.utils.Dialogs;

public class OpenInfo extends OpenCommand {

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_info;
    }

    public OpenInfo(AssistActivity act, String instruct) {
        super(act, instruct, R.string.command_info, R.string.instruction_app);
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
