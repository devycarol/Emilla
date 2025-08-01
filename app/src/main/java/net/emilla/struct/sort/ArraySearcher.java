package net.emilla.struct.sort;

import java.util.Collection;

public final class ArraySearcher<E extends Searchable<E>> {

    private final SearchableArray<E> mData;
    private final SearchableArray<SearchResult<E>> mSearchCache;

    public ArraySearcher(Collection<E> c) {
        mData = new SearchableArray<>(c);
        mSearchCache = new SearchableArray<>(16);
    }

    public FilterResult<E> search(String query) {
        if (query == null) return new Unfilter<>(mData);

        SearchResult<E> cachedResult = mSearchCache.get(query);
        if (cachedResult != null) return cachedResult;

        for (int i = query.length() - 1; i > 0; --i) {
            cachedResult = mSearchCache.get(query.substring(0, i));
            if (cachedResult != null) {
                return cache(cachedResult.narrow(query));
            }
        }

        return cache(mData.filter(query));
    }

    private SearchResult<E> cache(SearchResult<E> result) {
        mSearchCache.add(result);
        return result;
    }
}
