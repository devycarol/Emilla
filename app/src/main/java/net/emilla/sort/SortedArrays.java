package net.emilla.sort;

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

    /*internal*/ static <E> ArrayWindow<E> windowMatching(
        E[] array,
        Comparable<? super E> searcher
    ) {
        return windowMatching(array, searcher, 0, array.length);
    }

    /*internal*/ static <E> ArrayWindow<E> windowMatching(
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

    private SortedArrays() {}

}
