package net.emilla.command.app;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.annotation.internal;
import net.emilla.util.Intents;
import net.emilla.wadget.ActionSurface;

final class AppSearch extends AppCommand {
    @internal AppSearch(Context ctx, AppEntry appEntry) {
        super(ctx, appEntry, EditorInfo.IME_ACTION_SEARCH);
    }

    @Override
    protected Feedback run(ActionSurface surface, String query) {
        var res = surface.getResources();
        String[] searchAliases = res.getStringArray(R.array.subcmd_search);
        String lcQuery = query.toLowerCase();
        Intent search = Intents.searchToApp(this.appEntry.pkg);
        for (String alias : searchAliases) {
            if (lcQuery.startsWith(alias)) {
                // Todo: visual indication that this will be used
                query = query.substring(alias.length()).trim();
                if (!query.isEmpty()) {
                    search.putExtra(SearchManager.QUERY, query);
                }
                return Feedback.succeed(search);
            }
        }

        return Feedback.succeed(
            search.putExtra(SearchManager.QUERY, query)
        );
    }
}
