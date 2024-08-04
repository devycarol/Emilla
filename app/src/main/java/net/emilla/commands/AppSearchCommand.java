package net.emilla.commands;

import android.app.SearchManager;
import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;

public class AppSearchCommand extends AppCommand {
private final Intent mSearchIntent;

public AppSearchCommand(final AssistActivity act, final CharSequence appLabel, final Intent launch,
        final Intent search, @StringRes final int instructionId) {
    super(act, appLabel, specificTitle(act, appLabel, instructionId), launch);
    mSearchIntent = search;
}

@Override
public int imeAction() {
    return EditorInfo.IME_ACTION_SEARCH;
}

@Override
public void run(final String query) {
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
