package net.emilla.util;

import static android.app.SearchManager.QUERY;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import net.emilla.lang.Lang;
import net.emilla.lang.Words;
import net.emilla.util.trie.HashTrieMap;
import net.emilla.util.trie.TrieMap;

import java.util.Arrays;

public final class SearchEngineParser {

    /**
     * A record for a website's site URL and, if applicable, its search URL.
     *
     * @param siteUrl the website's main page.
     * @param searchUrl the website's search URL, with placeholder "%s" for the search query.
     */
    private record Website(@NonNull String siteUrl, String searchUrl)
            implements TrieMap.Value<Website> {

        @Override
        public boolean isPrefixable() {
            return searchUrl != null;
        }

        @Override
        public Website duplicate(Website value) {
            return this; // just return the self, discarding the duplicate value
            // TODO: don't allow duplicate bookmarks in the settings.
        }
    }

    private final TrieMap<String, Website> mEngineMap = new HashTrieMap<>();

    @Deprecated
    public SearchEngineParser(String engineCsv) {
        for (String entry : engineCsv.split("\\s*\n\\s*")) {
            var split = entry.split("\\s*,\\s*");
            if (split.length < 2) continue;

            int lastIdx = split.length - 1;
            String[] aliases = Arrays.copyOf(split, lastIdx);
            String url = split[lastIdx];

            var engine = url.contains("%s") ? new Website(url.replaceFirst("%s", ""), url)
                    : new Website(url, null);

            for (String alias : aliases) mEngineMap.put(Lang.words(alias), engine);
        }
    }

    public Intent intent(String query) {
        Words queryWords = Lang.words(query);
        Website get = mEngineMap.get(queryWords);
        if (get != null) {
            if (queryWords.hasRemainingContents()) {
                String encodedQuery = Uri.encode(queryWords.remainingContents());
                Uri uri = Uri.parse(get.searchUrl.replaceFirst("%s", encodedQuery));
                return Apps.viewTask(uri);
            } else return Apps.viewTask(Uri.parse(get.siteUrl));
        }
        return new Intent(Intent.ACTION_WEB_SEARCH).putExtra(QUERY, query);
    }
}
