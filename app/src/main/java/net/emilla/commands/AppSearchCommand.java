package net.emilla.commands;

import android.app.SearchManager;
import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Apps;

public class AppSearchCommand extends AppCommand {
private final Intent mSearchIntent;

public AppSearchCommand(final AssistActivity act, final String instruct, final AppCmdInfo info,
        @StringRes final int instructionId) {
    super(act, instruct, info, specificTitle(act, info.label, instructionId));
    mSearchIntent = Apps.searchTask(info.pkg);
}

@Override
public int imeAction() {
    return EditorInfo.IME_ACTION_SEARCH;
}

@Override
protected void run(final String query) {
    // Todo YouTube: instantly pull up bookmarked videos, specialized search for channels, playlists,
    //  etc. I assume the G assistant has similar functionality. If requires internet, could use
    //  bookmarks at the very least. Also, this command is broken for YouTube when a video is playing.
    final String[] searchAliases = resources().getStringArray(R.array.subcmd_search);
    final String lcQuery = query.toLowerCase();
    for (final String alias : searchAliases)
    if (lcQuery.startsWith(alias)) {
        // Todo livecmd: visual indication that this will be used
        final String actualQuery = query.substring(alias.length()).trim();
        if (!actualQuery.isEmpty()) mSearchIntent.putExtra(SearchManager.QUERY, actualQuery);
        succeed(mSearchIntent);
        return;
    }
    succeed(mSearchIntent.putExtra(SearchManager.QUERY, query));
}
}
