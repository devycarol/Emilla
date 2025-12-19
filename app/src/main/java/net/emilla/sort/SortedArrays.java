package net.emilla.sort;

import net.emilla.annotation.internal;
import net.emilla.util.ArrayLoader;

import java.util.Arrays;

public final class SortedArrays {

    public static <E> int indexOf(E[] array, Comparable<? super E> searcher) {
        int lo = 0;
        int hi = array.length - 1;

        while (lo <= hi) {
            int mid = lo + hi >>> 1;
            int cmp = searcher.compareTo(array[mid]);

            if (cmp > 0) {
                lo = mid + 1;
            } else if (cmp < 0) {
                hi = mid - 1;
            } else {
                return mid;
            }
        }

        return ~lo;
    }

    public static <E> int indexFor(E[] array, Comparable<? super E> searcher) {
        int index = indexOf(array, searcher);
        return index >= 0
            ? index
            : ~index;
    }

    @internal static <E> ArrayWindow<E> windowMatching(
        E[] array,
        Comparable<? super E> searcher
    ) {
        return windowMatching(array, searcher, 0, array.length);
    }

    @internal static <E> ArrayWindow<E> windowMatching(
        ArrayWindow<? extends E> window,
        Comparable<? super E> searcher
    ) {
        return windowMatching(window.mArray, searcher, window.mStart, window.mEnd);
    }

    private static <E> ArrayWindow<E> windowMatching(
        E[] array,
        Comparable<? super E> searcher,
        int start,
        int end
    ) {
        int lo = start;
        int hi = end - 1;

        while (lo <= hi) {
            int mid = lo + hi >>> 1;
            int cmp = searcher.compareTo(array[mid]);

            if (cmp > 0) {
                lo = mid + 1;
            } else if (cmp < 0) {
                hi = mid - 1;
            } else {
                return new ArrayWindow<E>(
                    array,
                    firstIndexOf(array, searcher, lo, mid),
                    lastIndexOf(array, searcher, mid, hi) + 1
                );
            }
        }

        return ArrayWindow.closed(array, lo);
    }

    private static <E> int firstIndexOf(E[] array, Comparable<? super E> searcher, int lo, int hi) {
        int first = -1;

        while (lo <= hi) {
            int mid = lo + hi >>> 1;
            int cmp = searcher.compareTo(array[mid]);

            if (cmp > 0) {
                lo = mid + 1;
            } else {
                if (cmp == 0) {
                    first = mid;
                }
                // keep searching the lower half
                hi = mid - 1;
            }
        }

        return first;
    }

    private static <E> int lastIndexOf(E[] array, Comparable<? super E> searcher, int lo, int hi) {
        int last = -1;

        while (lo <= hi) {
            int mid = lo + hi >>> 1;
            int cmp = searcher.compareTo(array[mid]);

            if (cmp < 0) {
                hi = mid - 1;
            } else {
                if (cmp == 0) {
                    last = mid;
                }
                // keep searching the upper half
                lo = mid + 1;
            }
        }

        return last;
    }

    /// Locations of step-sequences in a distinct, sorted array.
    ///
    /// @param sortedIndices must be sorted and distinct.
    /// @return a sorted array of all contiguous index portions in `sortedIndices` where the
    /// elements follow (n, n + 1, n + 2, ...). An element with no sequential neighbors will be a
    /// lone [IndexPortion].
    public static IndexPortion[] portions(int[] sortedIndices) {
        int indexCount = sortedIndices.length;
        var loader = new ArrayLoader<IndexPortion>(indexCount, IndexPortion[]::new);

        int last = indexCount - 1;
        for (int i = 0; i <= last; ++i) {
            int index = sortedIndices[i];
            if (i == last || index + 1 != sortedIndices[i + 1]) {
                loader.add(new IndexPortion(index));
            } else {
                do ++i;
                while (i < last && sortedIndices[i] + 1 == sortedIndices[i + 1]);

                int end = sortedIndices[i] + 1;
                loader.add(IndexSpan.window(index, end));
            }
        }

        return loader.array();
    }

    public static <E> E[] extract(E[] array, IndexPortion[] sortedPortions) {
        int position = 0;

        for (IndexPortion keep : sortedPortions) {
            position = keep.shiftTo(array, position);
        }

        return Arrays.copyOf(array, position);
    }

    private SortedArrays() {}

}
