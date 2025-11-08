package net.emilla.struct.sort;

import static net.emilla.BuildConfig.DEBUG;

import androidx.annotation.Nullable;

import java.util.Iterator;
import java.util.stream.Stream;

public final class SearchResult<E extends Searchable<E>>
    implements Searchable<SearchResult<E>>, FilterResult<E> {

    private final String mSearch;
    @Nullable
    private final SearchableArray<E>.Window mPrefixedWindow;
    @Nullable
    private final SearchableArray<E> mContainsElements;

    public SearchResult(
        String search,
        @Nullable SearchableArray<E>.Window prefixedWindow,
        @Nullable SearchableArray<E> containsElements
    ) {
        mSearch = search;
        mPrefixedWindow = prefixedWindow;
        mContainsElements = containsElements;
    }

    public SearchResult<E> narrow(String prefixedSearch) {
        if (DEBUG && !prefixedSearch.startsWith(mSearch)) {
            throw new IllegalArgumentException(
                '"' + prefixedSearch + "\" does not start with \"" + mSearch + '"'
            );
        }

        SearchableArray<E>.Window newPrefixedWindow = mPrefixedWindow != null
            ? mPrefixedWindow.prefixedBy(prefixedSearch)
            : null;
        SearchableArray<E> newContainsElements = mContainsElements != null
            ? mContainsElements.elementsContaining(prefixedSearch)
            : null;

        return new SearchResult<E>(prefixedSearch, newPrefixedWindow, newContainsElements);
    }

    private int prefixedCount() {
        return mPrefixedWindow != null ? mPrefixedWindow.size() : 0;
    }

    private int containsCount() {
        return mContainsElements != null ? mContainsElements.size() : 0;
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

        if (index < prefCount) {
            return mPrefixedWindow.get(index);
        }
        return mContainsElements.get(index - prefCount);
    }

    @Override
    public int size() {
        return prefixedCount() + containsCount();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Stream<E> stream() {
        if (mPrefixedWindow != null && mContainsElements != null) {
            return Stream.concat(mPrefixedWindow.stream(), mContainsElements.stream());
        }
        if (mPrefixedWindow != null) {
            return mPrefixedWindow.stream();
        }
        if (mContainsElements != null) {
            return mContainsElements.stream();
        }
        return Stream.empty();
    }

    @Override
    public Iterator<E> iterator() {
        return stream().iterator();
    }

}
