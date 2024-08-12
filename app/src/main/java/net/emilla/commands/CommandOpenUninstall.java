package net.emilla.commands;

import android.app.AlertDialog;
import android.content.Intent;

import androidx.annotation.DrawableRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Apps;
import net.emilla.utils.Dialogs;

public class CommandOpenUninstall extends OpenCommand {
@Override @DrawableRes
public int icon() {
    return R.drawable.ic_uninstall;
}

@Override
protected AlertDialog.Builder getAppChooser(final AssistActivity act) {
    return Dialogs.appUninstaller(act, mAppList);
}

public CommandOpenUninstall(final AssistActivity act) {
    super(act, R.string.command_uninstall, R.string.instruction_app);
}

@Override
public void run() {
    offer(mAppChooser.create());
}

@Override
protected Intent getIntent(final String pkg, final String cls) {
    return Apps.uninstallIntent(pkg, mPm);
}
}
