package net.emilla.struct.sort;

import androidx.annotation.Nullable;
import androidx.core.util.Function;
import androidx.core.util.Predicate;

import net.emilla.struct.IndexedStruct;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A simple growable array that sorts at insertion. This structure is not size-overflow safe!
 *
 * @param <E> comparable type for the array elements.
 */
public /*open*/ class SortedArray<E extends Comparable<E>> implements Iterable<E>, IndexedStruct<E> {

    protected E[] mData;
    protected int mSize;

    @SuppressWarnings("unchecked")
    public SortedArray(int initialCapacity) {
        if (initialCapacity < 0) throw new IllegalArgumentException();
        mData = (E[]) new Comparable[initialCapacity];
    }

    @SuppressWarnings("unchecked")
    public SortedArray(Collection<E> c) {
        mData = (E[]) new Comparable[c.size()];
        for (E val : c) addInternal(val);
    }

    @SuppressWarnings("unchecked")
    public <T> SortedArray(Collection<T> c, Function<T, E> converter) {
        mData = (E[]) new Comparable[c.size()];
        for (T val : c) addInternal(converter.apply(val));
    }

    @SuppressWarnings("unchecked")
    protected SortedArray(SortedArray<E> sarr, Predicate<E> takeIf) {
        mData = (E[]) new Comparable[sarr.mSize];
        for (E val : this) {
            if (takeIf.test(val)) addInternal(val);
        }
    }

    @SuppressWarnings("unchecked")
    protected SortedArray(SortedArray<E> sarr, Predicate<E> takeIf, IndexWindow exclude) {
        mData = (E[]) new Comparable[sarr.mSize];
        for (int i = 0; i < exclude.start; ++i) {
            if (takeIf.test(mData[i])) addInternal(mData[i]);
        }
        for (int i = exclude.end; i < mSize; ++i) {
            if (takeIf.test(mData[i])) addInternal(mData[i]);
        }
    }

    private void ensureCapacity() {
        if (mSize == mData.length) {
            mData = Arrays.copyOf(mData, mData.length * 3 / 2 + 1);
        }
    }

    /**
     * Adds {@code val} to the array, maintaining sorted order.
     *
     * @param val value to insert.
     * @return position the item was inserted at.
     */
    public int add(E val) {
        ensureCapacity();
        return addInternal(val);
    }

    private int addInternal(E val) {
        int pos;
        if (mSize == 0 || mData[mSize - 1].compareTo(val) < 0) {
            pos = mSize;
        } else {
            pos = indexFor(val);
            System.arraycopy(mData, pos, mData, pos + 1, mSize - pos);
        }

        mData[pos] = val;
        ++mSize;

        return pos;
    }

    @Override
    public E get(int index) {
        if (index >= mSize) {
            throw new IndexOutOfBoundsException(
                "Index " + index + " out of bounds for size " + mSize + "."
            );
        }
        return mData[index];
    }

    public boolean contains(E val) {
        return indexOf(val) >= 0;
    }

    @Nullable
    public E retrieve(E val) {
        int pos = indexOf(val);
        return pos >= 0 ? mData[pos] : null;
    }

    @Nullable
    public IndexWindow replace(E val, E replacement) {
        int pos = indexOf(val);
        if (pos < 0) return null;

        int repl = indexFor(replacement);
        if (pos < repl) {
            int shiftLen = repl - pos - 1;
            System.arraycopy(mData, pos + 1, mData, pos, shiftLen);
        } else if (repl < pos) {
            int shiftLen = pos - repl - 1;
            System.arraycopy(mData, repl, mData, repl + 1, shiftLen);
        }
        mData[repl] = replacement;

        return new IndexWindow(pos, repl);
    }

    /**
     * Removes {@code val} from the array.
     *
     * @param val value to remove.
     * @return the former position of the value, or -index - 1 of where the value would have been
     *         if the value wasn't found
     */
    public int remove(E val) {
        int pos = indexOf(val);

        if (pos >= 0) {
            --mSize;
            System.arraycopy(mData, pos + 1, mData, pos, mSize - pos);
            mData[mSize] = null;
        }

        return pos;
    }

    private int indexFor(E val) {
        int pos = indexOf(val);
        return pos < 0 ? ~pos : pos;
    }

    private int indexOf(E val) {
        int lo = 0;
        int hi = mSize - 1;

        while (lo <= hi) {
            int mid = lo + hi >>> 1;
            int cmp = mData[mid].compareTo(val);

            if (cmp < 0) lo = mid + 1;
            else if (cmp > 0) hi = mid - 1;
            else return mid; // value found.
        }

        return ~lo; // value not found.
    }

    protected int arbitraryIndexOf(Comparable<E> searcher) {
        int lo = 0;
        int hi = mSize - 1;

        while (lo <= hi) {
            int mid = lo + hi >>> 1;
            int cmp = searcher.compareTo(mData[mid]);

            if (cmp > 0) lo = mid + 1;
            else if (cmp < 0) hi = mid - 1;
            else return mid; // match found.
        }

        return ~lo; // match not found.
    }

    @Override
    public int size() {
        return mSize;
    }

    @Override
    public boolean isEmpty() {
        return mSize == 0;
    }

    public void trimToSize() {
        mData = Arrays.copyOf(mData, mSize);
    }

    @Nullable
    protected static <S extends SortedArray<E>, E extends Searchable<E>> S trim(S sarr) {
        if (sarr.isEmpty()) return null;
        sarr.trimToSize();
        return sarr;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private int pos = 0;

            @Override
            public boolean hasNext() {
                return pos < mSize;
            }

            @Override
            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                return mData[pos++];
            }
        };
    }

    public /*inner open*/ class Window implements IndexedStruct<E> {

        protected final IndexWindow window;

        public Window(IndexWindow window) {
            this.window = window;
        }

        @Override
        public E get(int index) {
            return SortedArray.this.get(window.arrayIndex(index));
        }

        @Override
        public int size() {
            return window.size();
        }

        @Override
        public boolean isEmpty() {
            return window.isEmpty();
        }
    }
}
