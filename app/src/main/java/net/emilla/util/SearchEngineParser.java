package net.emilla.util;

import static android.app.SearchManager.QUERY;
import static android.content.Intent.ACTION_WEB_SEARCH;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;

import net.emilla.lang.Lang;
import net.emilla.lang.Words;
import net.emilla.trie.TrieMap;

import java.util.Arrays;
import java.util.regex.Pattern;

public final class SearchEngineParser {

    private static final String WILDCARD = "%s";
    private static final Pattern WILDCARD_PATTERN = Pattern.compile(WILDCARD);

    /// A record for a website's site URL and, if applicable, its search URL.
    ///
    /// @param siteUrl the website's main page.
    /// @param searchUrl the website's search URL, with placeholder "%s" for the search query.
    private record Website(String siteUrl, @Nullable String searchUrl)
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

    private final TrieMap<String, Website> mEngineMap = new TrieMap<String, Website>();

    @Deprecated
    public SearchEngineParser(String engineCsv) {
        for (String entry : Patterns.TRIMMING_LINES.split(engineCsv)) {
            String[] split = Patterns.TRIMMING_CSV.split(entry);
            if (split.length < 2) continue;

            int last = split.length - 1;
            String[] aliases = Arrays.copyOf(split, last);
            String url = split[last];

            var engine = url.contains(WILDCARD)
                ? new Website(WILDCARD_PATTERN.matcher(url).replaceFirst(""), url)
                : new Website(url, null);

            for (String alias : aliases) {
                mEngineMap.put(Lang.words(alias), engine);
            }
        }
    }

    public Intent intent(String query) {
        Words queryWords = Lang.words(query);
        Website get = mEngineMap.get(queryWords);
        if (get != null) {
            if (queryWords.hasRemainingContents()) {
                String encodedQuery = Uri.encode(queryWords.remainingContents());
                String searchUrl = WILDCARD_PATTERN.matcher(get.searchUrl).replaceFirst(encodedQuery);
                return Intents.view(Uri.parse(searchUrl));
            }
            return Intents.view(Uri.parse(get.siteUrl));
        }
        return new Intent(ACTION_WEB_SEARCH).putExtra(QUERY, query);
    }
}
