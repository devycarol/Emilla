package net.emilla.sort;

import androidx.annotation.Nullable;

import net.emilla.annotation.internal;
import net.emilla.lang.Lang;
import net.emilla.struct.IndexedStruct;
import net.emilla.util.ArrayLoader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.IntFunction;

public final class ArraySearcher<E extends SearchItem> {

    private static <E extends SearchItem> E[] elementsContaining(
        E[] items,
        CharSequence normalizedSearch,
        ArrayWindow<?> exclude,
        IntFunction<E[]> generator
    ) {
        int itemCount = items.length;
        var loader = new ArrayLoader<E>(itemCount - exclude.size(), generator);

        int excludeStart = exclude.mStart;
        for (int i = 0; i < excludeStart; ++i) {
            E item = items[i];
            if (item.contains(normalizedSearch)) {
                loader.add(item);
            }
        }
        for (int i = exclude.mEnd; i < itemCount; ++i) {
            E item = items[i];
            if (item.contains(normalizedSearch)) {
                loader.add(item);
            }
        }

        return loader.array();
    }

    private final ArrayWrapper<E> mItems;
    private final HashMap<String, SearchResult<E>> mSearchCache;
    private final IntFunction<E[]> mArrayGenerator;

    @internal ArraySearcher(E[] sortedItems, IntFunction<E[]> arrayGenerator) {
        mItems = new ArrayWrapper<E>(sortedItems);
        mSearchCache = new HashMap<String, SearchResult<E>>(16);
        mArrayGenerator = arrayGenerator;
    }

    public static <E extends SearchItem>
    ArraySearcher<E> of(E[] items, IntFunction<E[]> arrayGenerator) {
        Arrays.sort(items);
        return new ArraySearcher<E>(items, arrayGenerator);
    }

    public IndexedStruct<E> search(@Nullable String search) {
        if (search == null || search.isEmpty()) {
            return mItems;
        }

        String normalizedSearch = Lang.normalize(search);
        SearchResult<E> cachedResult = mSearchCache.get(normalizedSearch);
        if (cachedResult != null) {
            return cachedResult;
        }

        int last = normalizedSearch.length() - 1;
        for (int i = last; i > 0; --i) {
            String prefix = normalizedSearch.substring(0, i);
            cachedResult = mSearchCache.get(prefix);

            if (cachedResult != null) {
                SearchResult<E> result = cachedResult.narrow(normalizedSearch, mArrayGenerator);
                return cache(normalizedSearch, result);
            }
        }

        E[] items = mItems.mArray;
        var searcher = new PrefixSearcher(normalizedSearch);

        ArrayWindow<E> primaryItems = SortedArrays.windowMatching(items, searcher);
        var result = new SearchResult<E>(
            primaryItems,
            elementsContaining(items, normalizedSearch, primaryItems, mArrayGenerator)
        );

        return cache(normalizedSearch, result);
    }

    private IndexedStruct<E> cache(String search, SearchResult<E> result) {
        mSearchCache.put(search, result);
        return result;
    }

    public void add(E item) {
        E[] oldItems = mItems.mArray;
        int position = SortedArrays.indexFor(oldItems, item);

        int itemCount = oldItems.length;
        E[] newItems = mArrayGenerator.apply(itemCount + 1);

        System.arraycopy(oldItems, 0, newItems, 0, position);
        newItems[position] = item;
        System.arraycopy(oldItems, position, newItems, position + 1, itemCount - position);

        setArray(newItems);
    }

    public void remove(E item) {
        E[] oldItems = mItems.mArray;
        int position = SortedArrays.indexOf(oldItems, item);
        if (position < 0) {
            return;
        }

        int newItemCount = oldItems.length - 1;
        E[] newItems = mArrayGenerator.apply(newItemCount);

        System.arraycopy(oldItems, 0, newItems, 0, position);
        System.arraycopy(oldItems, position + 1, newItems, position, newItemCount - position);

        setArray(newItems);
    }

    private void setArray(E[] newItems) {
        mItems.mArray = newItems;
        mSearchCache.clear();
    }

    public void load(E[] newItems) {
        Arrays.sort(newItems);
        setArray(newItems);
    }

    public ArrayWindow<E> itemsNamed(String search) {
        String normalizedSearch = Lang.normalize(search);
        Comparable<SearchItem> nameSearcher = item -> normalizedSearch.compareTo(item.mSearchKey);
        return SortedArrays.windowMatching(mItems.mArray, nameSearcher);
    }

}
