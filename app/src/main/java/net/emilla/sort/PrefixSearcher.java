package net.emilla.sort;

import net.emilla.annotation.internal;

final class PrefixSearcher implements Comparable<SearchItem> {

    private final String mNormalizedSearch;
    private final int mPrefixLength;

    @internal PrefixSearcher(String normalizedSearch) {
        mNormalizedSearch = normalizedSearch;
        mPrefixLength = normalizedSearch.length();
    }

    @Override
    public int compareTo(SearchItem item) {
        String searchKey = item.mSearchKey;

        if (searchKey.length() > mPrefixLength) {
            searchKey = searchKey.substring(0, mPrefixLength);
        }

        return mNormalizedSearch.compareTo(searchKey);
    }

}
