package net.emilla.command.app;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Resources;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.lang.Lang;
import net.emilla.util.Apps;

abstract class AppSearch extends AppCommand {

    AppSearch(AssistActivity act, AppSearchParams params) {
        super(act, params);
    }

    @Override
    protected void run(String query) {
        // Todo YouTube: instantly pull up bookmarked videos, specialized search for channels, playlists,
        //  etc. I assume the G assistant has similar functionality. If requires internet, could use
        //  bookmarks at the very least. Also, this command is broken for YouTube when a video is playing.
        String[] searchAliases = stringArray(R.array.subcmd_search);
        String lcQuery = query.toLowerCase();
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

    protected static abstract class AppSearchParams extends AppParams {

        @StringRes
        private final int mInstruction;

        protected AppSearchParams(Yielder info, @StringRes int instruction, @StringRes int summary) {
            super(info,
                  EditorInfo.IME_ACTION_SEARCH,
                  summary,
                  R.string.manual_app_search);
            // todo: the 'search' action shouldn't apply when just launching
            mInstruction = instruction;
        }

        @Override
        public final CharSequence title(Resources res) {
            return Lang.colonConcat(res, name, mInstruction);
        }
    }
}
