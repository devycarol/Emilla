package net.emilla.web;

import static android.app.SearchManager.QUERY;
import static android.content.Intent.ACTION_WEB_SEARCH;

import android.content.Intent;

import net.emilla.lang.Lang;
import net.emilla.lang.Words;
import net.emilla.trie.TrieMap;
import net.emilla.util.Patterns;

public final class WebsiteMap {

    private final TrieMap<String, Website> mSiteMap = new TrieMap<String, Website>();

    @Deprecated
    public WebsiteMap(String engineCsv) {
        for (String entry : Patterns.TRIMMING_LINES.split(engineCsv)) {
            String[] split = Patterns.TRIMMING_CSV.split(entry);
            if (split.length < 2) {
                continue;
            }

            int last = split.length - 1;

            String url = split[last];
            var site = Website.of(url);

            for (int i = 0; i < last; ++i) {
                String alias = split[i];
                mSiteMap.put(Lang.words(alias), site);
            }
        }
    }

    public Intent intent(String search) {
        Words words = Lang.words(search);

        Website site = mSiteMap.get(words);
        if (site == null) {
            return new Intent(ACTION_WEB_SEARCH).putExtra(QUERY, search);
        }

        String query = words.hasRemainingContents()
            ? words.remainingContents()
            : null;

        return site.viewIntent(query);
    }

}
