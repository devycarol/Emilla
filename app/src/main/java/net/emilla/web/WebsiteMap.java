package net.emilla.web;

import static android.app.SearchManager.QUERY;
import static android.content.Intent.ACTION_WEB_SEARCH;

import android.content.Intent;
import android.content.res.Resources;

import net.emilla.lang.Lang;
import net.emilla.trie.PhraseTree;
import net.emilla.trie.PrefixResult;
import net.emilla.util.Patterns;

public final class WebsiteMap {

    private final PhraseTree<Website> mSiteMap;

    @Deprecated
    public WebsiteMap(Resources res, CharSequence engineCsv) {
        mSiteMap = Lang.phraseTree(res, Website[]::new);

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
                mSiteMap.put(alias, site, site.hasSearchEngine());
            }
        }
    }

    public Intent intent(String search) {
        PrefixResult<Website, String> get = mSiteMap.get(search);

        Website site = get.value();
        if (site == null) {
            return new Intent(ACTION_WEB_SEARCH).putExtra(QUERY, search);
        }

        return site.viewIntent(get.leftovers);
    }

}
