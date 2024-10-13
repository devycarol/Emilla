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
protected AlertDialog.Builder getAppChooser(AssistActivity act) {
    return Dialogs.appUninstaller(act, mAppList);
}

public CommandOpenUninstall(AssistActivity act, String instruct) {
    super(act, instruct, R.string.command_uninstall, R.string.instruction_app);
}

@Override
protected void run() {
    offer(mAppChooser.create());
}

@Override
protected Intent getIntent(String pkg, String cls) {
    return Apps.uninstallIntent(pkg, mPm);
}
}
