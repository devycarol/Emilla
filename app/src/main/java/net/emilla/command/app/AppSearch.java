package net.emilla.command.app;

import android.app.SearchManager;
import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.util.Apps;

/*internal*/ final class AppSearch extends AppCommand {

    /*internal*/ AppSearch(AssistActivity act, AppEntry appEntry) {
        super(act, appEntry, EditorInfo.IME_ACTION_SEARCH);
    }

    @Override
    protected void run(String query) {
        String[] searchAliases = stringArray(R.array.subcmd_search);
        String lcQuery = query.toLowerCase();
        Intent search = Apps.searchToApp(this.appEntry.pkg);
        for (String alias : searchAliases) {
            if (lcQuery.startsWith(alias)) {
                // Todo: visual indication that this will be used
                query = query.substring(alias.length()).trim();
                if (!query.isEmpty()) search.putExtra(SearchManager.QUERY, query);
                appSucceed(search);
                return;
            }
        }
        appSucceed(search.putExtra(SearchManager.QUERY, query));
    }

}
