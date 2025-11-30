package net.emilla.web;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;

import net.emilla.trie.TrieMap;
import net.emilla.util.Intents;

/*internal*/ final class Website implements TrieMap.Value<Website> {

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

    @Override
    public boolean isPrefixable() {
        return mSearchEngine != null;
    }

    @Override
    public Website duplicate(Website value) {
        return this; // just return the self, discarding the duplicate value
        // TODO: don't allow duplicate bookmarks in the settings.
    }

}
