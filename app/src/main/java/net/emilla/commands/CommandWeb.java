package net.emilla.commands;

import static android.app.SearchManager.QUERY;
import static android.content.Intent.ACTION_WEB_SEARCH;

import android.content.Intent;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.utils.Apps;

public class CommandWeb extends CoreCommand {
private final Intent mIntent = Apps.newTask(ACTION_WEB_SEARCH);
private final boolean mUnsafe; // todo: handle at mapping and remove
private final Command mCmd; // Todo: configurable default command

public CommandWeb(final AssistActivity act, final Command cmd) {
    super(act, R.string.command_web, R.string.instruction_web);

    mUnsafe = mIntent.resolveActivity(packageManager()) == null;
    mCmd = cmd;
}

@Override
public Command cmd() {
    return mCmd;
}

@Override
public void run() {
    if (mUnsafe) throw new EmlaAppsException("No app found for web search.");
    succeed(mIntent);
}

@Override
public void run(final String searchOrUrl) {
    if (mUnsafe) throw new EmlaAppsException("No app found for web search.");
    succeed(mIntent.putExtra(QUERY, searchOrUrl));
}
}
