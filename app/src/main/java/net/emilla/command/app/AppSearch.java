package net.emilla.command.app;

import android.app.SearchManager;
import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.app.Apps;

/*internal open*/ class AppSearch extends AppCommand {

    /*internal*/ AppSearch(AssistActivity act, Yielder info) {
        this(act, info, R.string.instruction_search, R.string.summary_app_search);
        // todo: the 'send' action shouldn't apply when just launching
    }

    /*internal*/ AppSearch(AssistActivity act, Yielder info, @StringRes int instruction,
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
        String[] searchAliases = stringArray(R.array.subcmd_search);
        String lcQuery = query.toLowerCase();
        Intent in = Apps.searchToApp(pApp.pkg);
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
