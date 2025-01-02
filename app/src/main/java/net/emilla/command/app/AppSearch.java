package net.emilla.command.app;

import android.app.SearchManager;
import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Apps;

public class AppSearch extends AppCommand {

    private final Intent mSearchIntent;

    public AppSearch(AssistActivity act, String instruct, AppCmdInfo info,
            @StringRes int instructionId) {
        super(act, instruct, info, specificTitle(act, info.label, instructionId));
        mSearchIntent = Apps.searchTask(info.pkg);
    }

    @Override
    public int imeAction() {
        return EditorInfo.IME_ACTION_SEARCH;
    }

    @Override
    protected void run(String query) {
        // Todo YouTube: instantly pull up bookmarked videos, specialized search for channels, playlists,
        //  etc. I assume the G assistant has similar functionality. If requires internet, could use
        //  bookmarks at the very least. Also, this command is broken for YouTube when a video is playing.
        String[] searchAliases = stringArray(R.array.subcmd_search);
        String lcQuery = query.toLowerCase();
        for (String alias : searchAliases)
        if (lcQuery.startsWith(alias)) {
            // Todo livecmd: visual indication that this will be used
            String actualQuery = query.substring(alias.length()).trim();
            if (!actualQuery.isEmpty()) mSearchIntent.putExtra(SearchManager.QUERY, actualQuery);
            appSucceed(mSearchIntent);
            return;
        }
        appSucceed(mSearchIntent.putExtra(SearchManager.QUERY, query));
    }
}
