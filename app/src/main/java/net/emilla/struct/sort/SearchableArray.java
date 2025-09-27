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

    @Override @SuppressWarnings("unchecked")
    protected E[] newArray(int capacity) {
        return (E[]) new Searchable[capacity];
    }

    @Nullable
    public E get(String search) {
        int pos = arbitraryIndexOf(new ExactSearcher<>(search));
        return pos >= 0 ? pData[pos] : null;
    }

    public SearchResult<E> filter(String search) {
        IndexWindow prefixedWindow = windowMatching(new PrefixSearcher<>(search));
        SparseWindow containsWindow = elementsContaining(search, prefixedWindow);
        var prefWindow = prefixedWindow != null ? new Window(prefixedWindow) : null;

        return new SearchResult<>(search, prefWindow, containsWindow);
    }

    @Nullable
    private SparseWindow elementsContaining(String search, @Nullable IndexWindow exclude) {
        if (exclude == null) {
            return elements(val -> val.ordinalContains(search));
        }
        return elements(val -> val.ordinalContains(search), exclude);
    }

    @Nullable
    private IndexWindow windowMatching(Comparable<E> searcher) {
        return windowMatching(searcher, 0, pSize - 1);
    }

    @Nullable
    private IndexWindow windowMatching(Comparable<E> searcher, int lo, int hi) {
        while (lo <= hi) {
            int mid = lo + hi >>> 1;
            int cmp = searcher.compareTo(pData[mid]);

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
            int cmp = searcher.compareTo(pData[mid]);

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
            int cmp = searcher.compareTo(pData[mid]);

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

    @Nullable
    private SparseWindow elements(Predicate<E> takeIf) {
        var elements = new SparseWindow(takeIf);
        return elements.isEmpty() ? null : elements;
    }

    @Nullable
    private SparseWindow elements(Predicate<E> takeIf, IndexWindow exclude) {
        var elements = new SparseWindow(takeIf, exclude);
        return elements.isEmpty() ? null : elements;
    }

    public /*inner*/ final class SparseWindow extends SortedArray<E>.SparseWindow {

        private SparseWindow(Predicate<E> takeIf) {
            super(takeIf);
        }

        private SparseWindow(Predicate<E> takeIf, IndexWindow exclude) {
            super(takeIf, exclude);
        }

        private SparseWindow(SparseWindow elements, Predicate<E> takeIf) {
            super(elements, takeIf);
        }

        private SparseWindow(SparseWindow elements, Predicate<E> takeIf, IndexWindow exclude) {
            super(elements, takeIf, exclude);
        }

        @Nullable
        private SparseWindow elements(Predicate<E> takeIf) {
            var elements = new SparseWindow(this, takeIf);
            return elements.isEmpty() ? null : elements;
        }

        @Nullable
        private SparseWindow elements(Predicate<E> takeIf, IndexWindow exclude) {
            var elements = new SparseWindow(this, takeIf, exclude);
            return elements.isEmpty() ? null : elements;
        }

        @Nullable
        public SparseWindow elementsContaining(String search) {
            return elements(val -> val.ordinalContains(search));
        }
    }
}
