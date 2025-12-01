package net.emilla.web;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;

import net.emilla.util.Intents;

/*internal*/ final class Website {

    private final String mUrl;
    @Nullable
    private final SearchEngine mSearchEngine;

    private Website(String url, @Nullable SearchEngine searchEngine) {
        mUrl = url;
        mSearchEngine = searchEngine;
    }

    @Deprecated
    /*internal*/ static Website of(String url) {
        var engine = SearchEngine.of(url);
        return engine != null
            ? new Website(engine.rawUrl(), engine)
            : new Website(url, null);
    }

    /*internal*/ Intent viewIntent(@Nullable String searchQuery) {
        return Intents.view(
            searchQuery != null
                ? mSearchEngine.searchUrl(searchQuery)
                : Uri.parse(mUrl)
        );
    }

    public boolean hasSearchEngine() {
        return mSearchEngine != null;
    }

}
