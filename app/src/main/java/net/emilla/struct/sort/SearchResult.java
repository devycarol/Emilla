package net.emilla.struct.sort;

import static net.emilla.BuildConfig.DEBUG;

import androidx.annotation.Nullable;

public final class SearchResult<E extends Searchable<E>> extends Searchable<SearchResult<E>>
    implements FilterResult<E> {

    private final String mSearch;
    @Nullable
    private final SearchableArray<E>.Window mPrefixedWindow;
    @Nullable
    private final SearchableArray<E>.SparseWindow mContainsWindow;

    public SearchResult(
        String search,
        @Nullable SearchableArray<E>.Window prefixedWindow,
        @Nullable SearchableArray<E>.SparseWindow containsWindow
    ) {
        mSearch = search;
        mPrefixedWindow = prefixedWindow;
        mContainsWindow = containsWindow;
    }

    public SearchResult<E> narrow(String prefixedSearch) {
        if (DEBUG && !prefixedSearch.startsWith(mSearch)) {
            throw new IllegalArgumentException(
                "\"" + prefixedSearch + "\" does not start with \"" + mSearch + "\""
            );
        }

        SearchableArray<E>.Window newPrefixedWindow =
            mPrefixedWindow != null ? mPrefixedWindow.prefixedBy(prefixedSearch) : null;
        SearchableArray<E>.SparseWindow newContainsWindow =
            mContainsWindow != null ? mContainsWindow.elementsContaining(prefixedSearch) : null;

        return new SearchResult<E>(prefixedSearch, newPrefixedWindow, newContainsWindow);
    }

    @Override
    public boolean onePreferredMatch() {
        int prefCount = prefixedCount();
        return prefCount == 1
            // one prefix match
            || prefCount == 0 && containsCount() == 1
            // one contains match
            || prefCount > 1 && mPrefixedWindow.get(0).ordinalIs(mSearch)
                             && !mPrefixedWindow.get(1).ordinalIs(mSearch);
            // one exact match
    }

    @Override
    public String ordinal() {
        return mSearch;
    }

    @Override
    public E get(int index) {
        int prefCount = prefixedCount();
        int size = prefCount + containsCount();

        if (index < 0 || size <= index) {
            throw new IndexOutOfBoundsException("Index " + index + " out of range for size " + size);
        }

        if (index < prefCount) return mPrefixedWindow.get(index);
        return mContainsWindow.get(index - prefCount);
    }

    @Override
    public int size() {
        return prefixedCount() + containsCount();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    private int prefixedCount() {
        return mPrefixedWindow != null ? mPrefixedWindow.size() : 0;
    }

    private int containsCount() {
        return mContainsWindow != null ? mContainsWindow.size() : 0;
    }
}
