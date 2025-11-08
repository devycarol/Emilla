package net.emilla.struct.sort;

import androidx.annotation.Nullable;
import androidx.core.util.Function;

import java.util.Collection;

public final class SearchableArray<E extends Searchable<E>> extends SortedArray<E> {

    public SearchableArray(int initialCapacity) {
        super(initialCapacity);
    }

    public SearchableArray(Collection<? extends E> c) {
        super(c);
    }

    public <T> SearchableArray(Collection<T> c, Function<T, ? extends E> converter) {
        super(c, converter);
    }

    private SearchableArray(E[] data, int size) {
        super(data, size);
    }

    @Override @SuppressWarnings("unchecked")
    protected E[] newArray(int capacity) {
        return (E[]) new Searchable[capacity];
    }

    @Nullable
    public E get(String search) {
        int pos = arbitraryIndexOf(new ExactSearcher<E>(search));
        return pos >= 0 ? pData[pos] : null;
    }

    public SearchResult<E> filter(String search) {
        IndexWindow prefixedWindow = windowMatching(new PrefixSearcher<E>(search));
        SearchableArray<E> containsElements = elementsContaining(search, prefixedWindow);
        var prefWindow = prefixedWindow != null ? new Window(prefixedWindow) : null;

        return new SearchResult<E>(search, prefWindow, containsElements);
    }

    @Nullable
    public SearchableArray<E> elementsContaining(String search) {
        E[] data = newArray(pSize);
        int size = 0;

        for (int i = 0; i < pSize; ++i) {
            if (pData[i].ordinalContains(search)) {
                data[size] = pData[i];
                ++size;
            }
        }

        if (size == 0) return null;

        return new SearchableArray<E>(data, size);
    }

    @Nullable
    private SearchableArray<E> elementsContaining(String search, @Nullable IndexWindow exclude) {
        if (exclude == null) return elementsContaining(search);

        E[] data = newArray(pSize);
        int size = 0;

        for (int i = 0; i < exclude.start; ++i) {
            if (pData[i].ordinalContains(search)) {
                data[size] = pData[i];
                ++size;
            }
        }

        for (int i = exclude.end; i < pSize; ++i) {
            if (pData[i].ordinalContains(search)) {
                data[size] = pData[i];
                ++size;
            }
        }

        if (size == 0) return null;

        return new SearchableArray<E>(data, size);
    }

    @Nullable
    private IndexWindow windowMatching(Comparable<? super E> searcher) {
        return windowMatching(searcher, 0, pSize - 1);
    }

    @Nullable
    private IndexWindow windowMatching(Comparable<? super E> searcher, int lo, int hi) {
        while (lo <= hi) {
            int mid = lo + hi >>> 1;
            int cmp = searcher.compareTo(pData[mid]);

            if (cmp > 0) {
                lo = mid + 1;
            } else if (cmp < 0) {
                hi = mid - 1;
            } else {
                return new IndexWindow(
                    firstIndexOf(searcher, lo, mid),
                    lastIndexOf(searcher, mid, hi)
                );
            }
        }

        return null; // no values found.
    }

    private int firstIndexOf(Comparable<? super E> searcher, int lo, int hi) {
        int first = -1;

        while (lo <= hi) {
            int mid = lo + hi >>> 1;
            int cmp = searcher.compareTo(pData[mid]);

            if (cmp > 0) {
                lo = mid + 1;
            } else {
                if (cmp == 0) {
                    first = mid;
                }
                hi = mid - 1; // keep searching the lower half.
            }
        }

        return first;
    }

    private int lastIndexOf(Comparable<? super E> searcher, int lo, int hi) {
        int last = -1;

        while (lo <= hi) {
            int mid = lo + hi >>> 1;
            int cmp = searcher.compareTo(pData[mid]);

            if (cmp < 0) {
                hi = mid - 1;
            } else {
                if (cmp == 0) {
                    last = mid;
                }
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
                new PrefixSearcher<E>(search),
                window.start,
                window.last
            );
            return prefixed != null ? new Window(prefixed) : null;
        }

    }

}
