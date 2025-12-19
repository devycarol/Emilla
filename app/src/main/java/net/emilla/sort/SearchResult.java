package net.emilla.sort;

import net.emilla.annotation.internal;
import net.emilla.struct.IndexedStruct;
import net.emilla.util.ArrayLoader;

import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public final class SearchResult<E extends SearchItem> implements IndexedStruct<E> {

    @internal final ArrayWindow<E> mPrimaryItems;
    private final E[] mSecondaryItems;

    @internal SearchResult(ArrayWindow<E> primaryItems, E[] secondaryItems) {
        mPrimaryItems = primaryItems;
        mSecondaryItems = secondaryItems;
    }

    @internal SearchResult<E> narrow(
        String normalizedTailedSearch,
        IntFunction<E[]> arrayGenerator
    ) {
        var searcher = new PrefixSearcher(normalizedTailedSearch);
        ArrayWindow<E> narrowed = SortedArrays.windowMatching(mPrimaryItems, searcher);

        E[] array = narrowed.mArray;
        var spliceBefore = new ArrayWindow<E>(array, mPrimaryItems.mStart, narrowed.mStart);
        var spliceAfter = new ArrayWindow<E>(array, narrowed.mEnd, mPrimaryItems.mEnd);

        int secondaryCount = mSecondaryItems.length;
        int spliceCount = spliceBefore.size() + spliceAfter.size();
        var loader = new ArrayLoader<E>(secondaryCount + spliceCount, arrayGenerator);

        if (spliceCount == 0) {
            for (E item : mSecondaryItems) {
                if (item.contains(normalizedTailedSearch)) {
                    loader.add(item);
                }
            }
        } else {
            E firstItem = (spliceBefore.isEmpty() ? spliceAfter : spliceBefore).get(0);
            int splicePosition = SortedArrays.indexFor(mSecondaryItems, firstItem);

            for (int i = 0; i < splicePosition; ++i) {
                E item = mSecondaryItems[i];
                if (item.contains(normalizedTailedSearch)) {
                    loader.add(item);
                }
            }
            for (E item : spliceBefore) {
                if (item.contains(normalizedTailedSearch)) {
                    loader.add(item);
                }
            }
            for (E item : spliceAfter) {
                if (item.contains(normalizedTailedSearch)) {
                    loader.add(item);
                }
            }
            for (int i = splicePosition; i < secondaryCount; ++i) {
                E item = mSecondaryItems[i];
                if (item.contains(normalizedTailedSearch)) {
                    loader.add(item);
                }
            }
        }

        return new SearchResult<E>(narrowed, loader.array());
    }

    @Override
    public E get(int index) {
        int primaryCount = mPrimaryItems.size();
        if (index < primaryCount) {
            return mPrimaryItems.get(index);
            // IOB for negative indices
        }

        index -= primaryCount;

        if (index < mSecondaryItems.length) {
            return mSecondaryItems[index];
        }

        throw new IndexOutOfBoundsException("Index out of range");
    }

    @Override
    public int size() {
        return mPrimaryItems.size() + mSecondaryItems.length;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Stream<E> stream() {
        return Stream.concat(mPrimaryItems.stream(), Arrays.stream(mSecondaryItems));
    }

}
