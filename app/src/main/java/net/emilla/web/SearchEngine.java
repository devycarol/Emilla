package net.emilla.web;

import android.net.Uri;

import androidx.annotation.Nullable;

final class SearchEngine {

    private static final String WILDCARD = "%s";

    private final String mUrlPrefix;
    private final String mUrlSuffix;

    private SearchEngine(String urlPrefix, String urlSuffix) {
        mUrlPrefix = urlPrefix;
        mUrlSuffix = urlSuffix;
    }

    @Deprecated @Nullable
    /*internal*/ static SearchEngine of(String searchUrl) {
        int wildcardPosition = searchUrl.indexOf(WILDCARD);
        if (wildcardPosition < 0) {
            return null;
        }

        return new SearchEngine(
            searchUrl.substring(0, wildcardPosition),
            searchUrl.substring(wildcardPosition + 2)
        );
    }

    public Uri searchUrl(String query) {
        return Uri.parse(mUrlPrefix + Uri.encode(query) + mUrlSuffix);
    }

    @Deprecated
    public String rawUrl() {
        return mUrlPrefix + mUrlSuffix;
    }

}
