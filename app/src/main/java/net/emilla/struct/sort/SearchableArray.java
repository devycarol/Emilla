package net.emilla.struct.sort;

import androidx.annotation.Nullable;
import androidx.core.util.Function;
import androidx.core.util.Predicate;

import java.util.Collection;

public final class SearchableArray<E extends Searchable<E>> extends SortedArray<E> {

    public SearchableArray(int initialCapacity) {
        super(initialCapacity);
    }

    public SearchableArray(Collection<E> c) {
        super(c);
    }

    public <T> SearchableArray(Collection<T> c, Function<T, E> converter) {
        super(c, converter);
    }

    private SearchableArray(SearchableArray<E> sarr, Predicate<E> takeIf) {
        super(sarr, takeIf);
    }

    private SearchableArray(SearchableArray<E> sarr, Predicate<E> takeIf, IndexWindow exclude) {
        super(sarr, takeIf, exclude);
    }

    @Nullable
    public E get(String search) {
        int pos = arbitraryIndexOf(new ExactSearcher<>(search));
        return pos >= 0 ? mData[pos] : null;
    }

    @Nullable
    private SearchableArray<E> elements(Predicate<E> takeIf) {
        return trim(new SearchableArray<>(this, takeIf));
    }

    @Nullable
    private SearchableArray<E> elements(Predicate<E> takeIf, IndexWindow exclude) {
        return trim(new SearchableArray<>(this, takeIf, exclude));
    }

    public SearchResult<E> filter(String search) {
        return filter(search, false);
    }

    public SearchResult<E> filter(String search, boolean isolateExact) {
        if (isolateExact) {
            IndexWindow exacts = windowMatching(new ExactSearcher<>(search));
            if (exacts != null) return new ExactWindowSearch<>(search, new Window(exacts));
        }

        IndexWindow prefixed = windowMatching(new PrefixSearcher<>(search));
        if (prefixed != null) {
            SearchableArray<E> contains = elementsContaining(search, prefixed);
            var prefWindow = new Window(prefixed);

            if (contains == null) return new WindowSearch<>(search, prefWindow);
            return new SectoredSearch<>(search, prefWindow, contains);
        }

        SearchableArray<E> contains = elementsContaining(search);
        if (contains == null) return new EmptyFilter<>(search);

        return new SparseSearch<>(search, contains);
    }

    @Nullable
    public SearchableArray<E> elementsContaining(String search) {
        return elements(val -> val.ordinalContains(search));
    }

    @Nullable
    private SearchableArray<E> elementsContaining(String search, IndexWindow prefixed) {
        return elements(val -> val.ordinalContains(search), prefixed);
    }

    @Nullable
    private IndexWindow windowMatching(Comparable<E> searcher) {
        return windowMatching(searcher, 0, mSize - 1);
    }

    @Nullable
    private IndexWindow windowMatching(Comparable<E> searcher, int lo, int hi) {
        while (lo <= hi) {
            int mid = lo + hi >>> 1;
            int cmp = searcher.compareTo(mData[mid]);

            if (cmp > 0) lo = mid + 1;
            else if (cmp < 0) hi = mid - 1;
            else return new IndexWindow(firstIndexOf(searcher, lo, mid),
                                        lastIndexOf(searcher, mid, hi));
        }

        return null; // no values found.
    }

    private int firstIndexOf(Comparable<E> searcher, int lo, int hi) {
        int first = -1;

        while (lo <= hi) {
            int mid = lo + hi >>> 1;
            int cmp = searcher.compareTo(mData[mid]);

            if (cmp > 0) {
                lo = mid + 1;
            } else {
                if (cmp == 0) first = mid;
                hi = mid - 1; // keep searching the lower half.
            }
        }

        return first;
    }

    private int lastIndexOf(Comparable<E> searcher, int lo, int hi) {
        int last = -1;

        while (lo <= hi) {
            int mid = lo + hi >>> 1;
            int cmp = searcher.compareTo(mData[mid]);

            if (cmp < 0) {
                hi = mid - 1;
            } else {
                if (cmp == 0) last = mid;
                lo = mid + 1; // keep searching the upper half.
            }
        }

        return last;
    }

    public /*inner*/ final class Window extends SortedArray<E>.Window {

        public Window(IndexWindow window) {
            super(window);
        }

        @Nullable
        public Window prefixedBy(String search) {
            IndexWindow prefixed = windowMatching(
                new PrefixSearcher<>(search),
                window.start,
                window.last
            );
            return prefixed != null ? new Window(prefixed) : null;
        }
    }
}
