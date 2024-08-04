package net.emilla.commands;

import static android.provider.Settings.ACTION_SETTINGS;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.utils.Apps;

public class CommandSettings extends CoreCommand {
private final Intent mIntent = Apps.newTask(ACTION_SETTINGS);

@Override
public Command cmd() {
    return Command.SETTINGS;
}

@Override @ArrayRes
public int detailsId() {
    return R.array.details_settings;
}

@Override @DrawableRes
public int icon() {
    return R.drawable.ic_settings;
}

@Override
public int imeAction() {
    return EditorInfo.IME_ACTION_DONE;
}

public CommandSettings(final AssistActivity act) {
    super(act, R.string.command_settings, R.string.instruction_settings);
}

@Override
public void run() {
    if (mIntent.resolveActivity(packageManager()) == null) throw new EmlaAppsException("No app found for system settings."); // todo handle at mapping
    succeed(mIntent);
}

@Override
public void run(final String query) {
    // TODO: settings search and value-changing
    run();
}
}
