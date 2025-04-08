package net.emilla.util;

import androidx.annotation.Nullable;
import androidx.core.util.Function;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A simple growable array that sorts at insertion. This structure is not size-overflow safe!
 *
 * @param <E> comparable type for the array elements.
 */
public /*open*/ class SortedArray<E extends Comparable<E>> implements Iterable<E> {

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

    public E get(int index) {
        if (index >= mSize) {
            String msg = "Index " + index + " out of bounds for size " + mSize + ".";
            throw new IndexOutOfBoundsException(msg);
        }
        return mData[index];
    }

    public boolean contains(E val) {
        return indexOf(val) >= 0;
    }

    @Nullable
    public E retrieve(E val) {
        int pos = indexOf(val);
        return pos < 0 ? null : mData[pos];
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

    public boolean isEmpty() {
        return mSize == 0;
    }

    public int size() {
        return mSize;
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

}
