package net.emilla.commands;

import static android.app.SearchManager.QUERY;
import static android.content.Intent.ACTION_WEB_SEARCH;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.DrawableRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.utils.Apps;

public class CommandWeb extends CoreCommand {
private final Intent mIntent = Apps.newTask(ACTION_WEB_SEARCH);
private final boolean mUnsafe; // todo: handle at mapping and remove

public CommandWeb(final AssistActivity act) {
    super(act, R.string.command_web, R.string.instruction_web);
    mUnsafe = mIntent.resolveActivity(packageManager()) == null;
}

@Override @DrawableRes
public int icon() {
    return R.drawable.ic_web;
}

@Override
public int imeAction() {
    return EditorInfo.IME_ACTION_SEARCH;
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
