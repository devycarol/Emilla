package net.emilla.commands;

import static android.content.Intent.ACTION_DIAL;
import static android.net.Uri.parse;

import android.content.Intent;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.utils.Apps;

public class CommandDial extends CoreCommand {
private final Intent mIntent = Apps.newTask(ACTION_DIAL);

public CommandDial(final AssistActivity act) {
    super(act, R.string.command_dial, R.string.instruction_dial);
}

@Override
public Command cmd() {
    return Command.DIAL;
}

@Override
public void run() {
    if (packageManager().resolveActivity(mIntent, 0) == null) throw new EmlaAppsException("No dialer app found on your device."); // todo handle at mapping
    succeed(mIntent);
}

@Override
public void run(final String nameOrNumber) {
    if (packageManager().resolveActivity(mIntent, 0) == null) throw new EmlaAppsException("No dialer app found on your device."); // todo handle at mapping
    succeed(mIntent.setData(parse("tel:" + nameOrNumber)));
}
}
