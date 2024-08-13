package net.emilla.commands;

import android.app.AlertDialog;
import android.content.Intent;
import android.provider.Settings;

import androidx.annotation.DrawableRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.utils.Apps;
import net.emilla.utils.Dialogs;

public class CommandOpenInfo extends OpenCommand {
@Override @DrawableRes
public int icon() {
    return R.drawable.ic_info;
}

@Override
protected AlertDialog.Builder getAppChooser(final AssistActivity act) {
    return Dialogs.appChooser(act, act.getPackageManager(), mAppList);
}

private final Intent mInfoIntent = Apps.newTask(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Apps.pkgUri(Apps.PKG));
private final boolean mUnsafe;

public CommandOpenInfo(final AssistActivity act, final String instruct) {
    super(act, instruct, R.string.command_info, R.string.instruction_app);
    mUnsafe = mInfoIntent.resolveActivity(packageManager()) == null;
}

@Override
protected void run() {
    // Todo: it may be useful to include listings beyond those in the launcher icons, or be able to
    //  search by package name.
    if (mUnsafe) throw new EmlaAppsException("No settings app found for your device."); // Todo: handle at mapping
    succeed(mInfoIntent);
}

@Override
protected Intent getIntent(final String pkg, final String cls) {
    return Apps.newTask(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Apps.pkgUri(pkg));
}
}
