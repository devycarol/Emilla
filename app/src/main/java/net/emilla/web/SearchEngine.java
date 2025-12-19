package net.emilla.web;

import android.net.Uri;

import androidx.annotation.Nullable;

import net.emilla.annotation.internal;
import net.emilla.text.Csv;
import net.emilla.util.Strings;

import java.util.NoSuchElementException;

final class SearchEngine {

    @internal final String mUrlPrefix;
    @internal final String mUrlSuffix;

    private SearchEngine(@Nullable String urlPrefix, @Nullable String urlSuffix) {
        mUrlPrefix = Strings.emptyIfNull(urlPrefix);
        mUrlSuffix = Strings.emptyIfNull(urlSuffix);
    }

    @Nullable
    @internal static SearchEngine from(Csv csv) {
        String prefix = csv.next();
        String suffix = csv.next();
        return prefix != null || suffix != null
            ? new SearchEngine(prefix, suffix)
            : null;
    }

    @Deprecated
    @internal static SearchEngine fromRaw(String prefix, String suffix) {
        return new SearchEngine(prefix, suffix);
    }

    private static NoSuchElementException invalidWebsiteEntry() {
        return new NoSuchElementException("Invalid website entry");
    }

    public Uri url(String query) {
        return Uri.parse(mUrlPrefix + Uri.encode(query) + mUrlSuffix);
    }

}
