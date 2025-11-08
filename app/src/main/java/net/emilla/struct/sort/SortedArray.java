package net.emilla.struct.sort;

import androidx.annotation.Nullable;
import androidx.core.util.Function;

import net.emilla.struct.IndexedStruct;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/// A simple growable array that sorts at insertion. This structure is not size-overflow safe!
///
/// @param <E> comparable type for the array elements.
public /*open*/ class SortedArray<E extends Comparable<E>> implements Iterable<E>, IndexedStruct<E> {

    protected E[] pData;
    protected int pSize = 0;

    public SortedArray(int initialCapacity) {
        if (initialCapacity < 0) throw new IllegalArgumentException();
        pData = newArray(initialCapacity);
    }

    public SortedArray(Collection<E> c) {
        pData = newArray(c.size());
        for (E val : c) addInternal(val);
    }

    public <T> SortedArray(Collection<T> c, Function<T, E> converter) {
        pData = newArray(c.size());
        for (T val : c) addInternal(converter.apply(val));
    }

    protected SortedArray(E[] data, int size) {
        pData = Arrays.copyOf(data, size);
        pSize = size;
    }

    @SuppressWarnings("unchecked")
    protected /*open*/ E[] newArray(int capacity) {
        return (E[]) new Comparable[capacity];
    }

    private void ensureCapacity() {
        if (pSize == pData.length) {
            pData = Arrays.copyOf(pData, pData.length * 3 / 2 + 1);
        }
    }

    /// Adds `val` to the array, maintaining sorted order.
    ///
    /// @param val value to insert.
    /// @return position the item was inserted at.
    public final int add(E val) {
        ensureCapacity();
        return addInternal(val);
    }

    private int addInternal(E val) {
        int pos;
        if (pSize == 0 || pData[pSize - 1].compareTo(val) < 0) {
            pos = pSize;
        } else {
            pos = indexFor(val);
            System.arraycopy(pData, pos, pData, pos + 1, pSize - pos);
        }

        pData[pos] = val;
        ++pSize;

        return pos;
    }

    @Override
    public final E get(int index) {
        if (index >= pSize) {
            throw new IndexOutOfBoundsException(
                "Index " + index + " out of bounds for size " + pSize + '.'
            );
        }
        return pData[index];
    }

    public final boolean contains(E val) {
        return indexOf(val) >= 0;
    }

    @Nullable
    public final E retrieve(E val) {
        int pos = indexOf(val);
        return pos >= 0 ? pData[pos] : null;
    }

    @Nullable
    public final IndexWindow replace(E val, E replacement) {
        int pos = indexOf(val);
        if (pos < 0) return null;

        int repl = indexFor(replacement);
        if (pos < repl) {
            int shiftLen = repl - pos - 1;
            System.arraycopy(pData, pos + 1, pData, pos, shiftLen);
        } else if (repl < pos) {
            int shiftLen = pos - repl - 1;
            System.arraycopy(pData, repl, pData, repl + 1, shiftLen);
        }
        pData[repl] = replacement;

        return new IndexWindow(pos, repl);
    }

    /// Removes `val` from the array.
    ///
    /// @param val value to remove.
    /// @return the former position of the value, or -index - 1 of where the value would have been
    /// if the value wasn't found
    public final int remove(E val) {
        int pos = indexOf(val);

        if (pos >= 0) {
            --pSize;
            System.arraycopy(pData, pos + 1, pData, pos, pSize - pos);
            pData[pSize] = null;
        }

        return pos;
    }

    private int indexFor(E val) {
        int pos = indexOf(val);
        return pos < 0 ? ~pos : pos;
    }

    private int indexOf(E val) {
        int lo = 0;
        int hi = pSize - 1;

        while (lo <= hi) {
            int mid = lo + hi >>> 1;
            int cmp = pData[mid].compareTo(val);

            if (cmp < 0) lo = mid + 1;
            else if (cmp > 0) hi = mid - 1;
            else return mid; // value found.
        }

        return ~lo; // value not found.
    }

    protected final int arbitraryIndexOf(Comparable<E> searcher) {
        int lo = 0;
        int hi = pSize - 1;

        while (lo <= hi) {
            int mid = lo + hi >>> 1;
            int cmp = searcher.compareTo(pData[mid]);

            if (cmp > 0) lo = mid + 1;
            else if (cmp < 0) hi = mid - 1;
            else return mid; // match found.
        }

        return ~lo; // match not found.
    }

    public final void trimToSize() {
        pData = Arrays.copyOf(pData, pSize);
    }

    @Override
    public final int size() {
        return pSize;
    }

    @Override
    public final boolean isEmpty() {
        return pSize == 0;
    }

    @Override
    public final Stream<E> stream() {
        return Arrays.stream(pData, 0, pSize);
    }

    @Override
    public final Iterator<E> iterator() {
        return new Iterator<E>() {
            private int pos = 0;

            @Override
            public boolean hasNext() {
                return pos < pSize;
            }

            @Override
            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                return pData[pos++];
            }
        };
    }

    public /*inner open*/ class Window implements IndexedStruct<E> {

        public final IndexWindow window;

        public Window(IndexWindow window) {
            this.window = window;
        }

        @Override
        public final E get(int index) {
            return SortedArray.this.get(window.arrayIndex(index));
        }

        @Override
        public final int size() {
            return window.size;
        }

        @Override
        public final boolean isEmpty() {
            return window.isEmpty();
        }

        @Override
        public final Stream<E> stream() {
            return Arrays.stream(pData, window.start, window.end);
        }

        @Override
        public final Iterator<E> iterator() {
            return stream().iterator();
        }

    }

}
