package net.emilla.command.app;

import android.app.SearchManager;
import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.util.Apps;

/*internal*/ abstract class AppSearch extends AppCommand {

    public AppSearch(AssistActivity act, Yielder info, @StringRes int instruction,
            @StringRes int summary) {
        super(act, new InstructyParams(info, instruction),
              summary,
              R.string.manual_app_search,
              EditorInfo.IME_ACTION_SEARCH);
        // todo: the 'search' action shouldn't apply when just launching
    }

    @Override
    protected final void run(String query) {
        // Todo YouTube: instantly pull up bookmarked videos, specialized search for channels, playlists,
        //  etc. I assume the G assistant has similar functionality. If requires internet, could use
        //  bookmarks at the very least. Also, this command is broken for YouTube when a video is playing.
        var searchAliases = stringArray(R.array.subcmd_search);
        var lcQuery = query.toLowerCase();
        Intent in = Apps.searchTask(packageName);
        for (String alias : searchAliases) {
            if (lcQuery.startsWith(alias)) {
                // Todo livecmd: visual indication that this will be used
                query = query.substring(alias.length()).trim();
                if (!query.isEmpty()) in.putExtra(SearchManager.QUERY, query);
                appSucceed(in);
                return;
            }
        }
        appSucceed(in.putExtra(SearchManager.QUERY, query));
    }
}
