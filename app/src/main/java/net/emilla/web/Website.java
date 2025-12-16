package net.emilla.web;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;

import net.emilla.text.Csv;
import net.emilla.text.CsvBuilder;
import net.emilla.util.Intents;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Website {

    private static final Pattern LEGACY_SEARCH_WILDCARD = Pattern.compile("%s");

    private final String mUrl;
    @Nullable
    private final SearchEngine mSearchEngine;
    /*internal*/ String[] mAliases;

    private Website(String url, @Nullable SearchEngine searchEngine, String[] aliases) {
        mUrl = url;
        mSearchEngine = searchEngine;
        mAliases = aliases;
    }

    @Nullable
    /*internal*/ static Website from(Csv csv) {
        try {
            String url = csv.requireNext();
            var engine = SearchEngine.from(csv);
            String[] aliases = csv.remainingValues();

            return new Website(url, engine, aliases);
        } catch (NoSuchElementException e) {
            // broken entry
            return null;
        }
    }

    @Deprecated @Nullable
    /*internal*/ static Website fromLegacy(Csv csv) {
        String[] split;
        try {
            split = csv.remainingValues();
        } catch (NoSuchElementException e) {
            return null;
        }

        int last = split.length - 1;
        if (last < 1) {
            return null;
        }

        String[] aliases = Arrays.copyOf(split, last);

        String rawUrl = split[last];
        Matcher matcher = LEGACY_SEARCH_WILDCARD.matcher(rawUrl);

        String url;
        @Nullable SearchEngine engine;
        if (matcher.find()) {
            String prefix = rawUrl.substring(0, matcher.start());
            String suffix = rawUrl.substring(matcher.end());

            url = prefix + suffix;
            engine = SearchEngine.fromRaw(prefix, suffix);
        } else {
            url = rawUrl;
            engine = null;
        }

        return new Website(url, engine, aliases);
    }

    public boolean hasSearchEngine() {
        return mSearchEngine != null;
    }

    /*internal*/ Intent viewIntent(@Nullable String searchQuery) {
        return Intents.view(
            searchQuery != null
                ? mSearchEngine.url(searchQuery)
                : Uri.parse(mUrl)
        );
    }

    @Override
    public String toString() {
        var csv = new CsvBuilder(mUrl);

        if (mSearchEngine != null) {
            csv.append(mSearchEngine.mUrlPrefix, mSearchEngine.mUrlSuffix);
        } else {
            csv.append(null, null);
        }

        csv.append(mAliases);

        return csv.toString();
    }

}
