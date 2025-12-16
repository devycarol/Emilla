package net.emilla.web;

import static android.app.SearchManager.QUERY;
import static android.content.Intent.ACTION_WEB_SEARCH;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;

import net.emilla.config.SettingVals;
import net.emilla.lang.Lang;
import net.emilla.text.Csv;
import net.emilla.text.CsvLines;
import net.emilla.trie.PhraseTree;
import net.emilla.trie.PrefixResult;
import net.emilla.util.ArrayLoader;

import java.util.function.Function;

public final class WebsiteMap {

    private final PhraseTree<Website> mSiteMap;

    public WebsiteMap(SharedPreferences prefs, Resources res) {
        mSiteMap = Lang.phraseTree(res, Website[]::new);

        String engineCsv = SettingVals.searchEngineCsv(prefs);
        boolean isLegacy = engineCsv != null;

        CsvLines lines;
        Function<Csv, Website> mapper;
        if (isLegacy) {
            lines = new CsvLines(engineCsv);
            mapper = Website::fromLegacy;
        } else {
            lines = new CsvLines(SettingVals.websiteCsv(prefs));
            mapper = Website::from;
        }

        var loader = isLegacy
            ? new ArrayLoader<String>(10, String[]::new)
            : null;
        while (lines.hasNext()) {
            Csv csv = lines.next();
            Website site = mapper.apply(csv);
            if (site == null) {
                continue;
            }

            for (String alias : site.mAliases) {
                mSiteMap.put(alias, site, site.hasSearchEngine());
            }

            if (isLegacy) {
                loader.growingAdd(site.toString());
            }
        }

        if (isLegacy) {
            SettingVals.setWebsites(prefs, ArrayLoader.join(loader, '\n'));
        }
    }

    public Intent intent(String search) {
        PrefixResult<Website, String> get = mSiteMap.get(search);

        Website site = get.value();
        return site != null
            ? site.viewIntent(get.leftovers)
            : new Intent(ACTION_WEB_SEARCH).putExtra(QUERY, search);
    }

}
