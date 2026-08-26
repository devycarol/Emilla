package net.emilla.command.app;

import android.app.SearchManager;

import net.emilla.Feedback;
import net.emilla.annotation.internal;
import net.emilla.util.Intents;
import net.emilla.wadget.ActionSurface;

final class AppSearch extends AppCommand {
    @internal AppSearch(ActionSurface surface, AppEntry appEntry) {
        super(surface, appEntry);
    }

    @Override
    protected Feedback run(ActionSurface surface, String query) {
        return Feedback.succeed(Intents.searchToApp(this.appEntry.pkg)
            .putExtra(SearchManager.QUERY, query)
        );
    }
}
