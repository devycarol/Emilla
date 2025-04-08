package net.emilla.util;

import androidx.annotation.Nullable;
import androidx.core.util.Function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

    public List<E> filter(String search) {
        IndexWindow exacts = windowMatching(new ExactSearcher<>(search));
        if (exacts != null) return elements(exacts);

        IndexWindow prefixed = windowMatching(new PrefixSearcher<>(search));
        var filtered = new ArrayList<E>(mSize);
        if (prefixed != null) {
            filtered.addAll(0, elements(prefixed));
            E[] data = mData;
            for (int i = 0; i < prefixed.start; ++i) {
                E val = data[i];
                if (val.ordinalContains(search)) filtered.add(val);
            }
            for (int i = prefixed.end; i < mSize; ++i) {
                E val = data[i];
                if (val.ordinalContains(search)) filtered.add(val);
            }
        } else {
            for (E val : this) {
                if (val.ordinalContains(search)) filtered.add(val);
            }
        }

        return filtered;
    }

    @Nullable
    private IndexWindow windowMatching(Comparable<E> searcher) {
        int lo = 0;
        int hi = mSize - 1;

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

            if (cmp > 0) lo = mid + 1;
            else {
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

            if (cmp < 0) hi = mid - 1;
            else {
                if (cmp == 0) last = mid;
                lo = mid + 1; // keep searching the upper half.
            }
        }

        return last;
    }

    private List<E> elements(IndexWindow window) {
        return Arrays.asList(Arrays.copyOfRange(mData, window.start, window.end));
    }
}
