package net.emilla.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A simple growable array that sorts at insertion. This structure is not size-overflow safe!
 *
 * @param <E> comparable type for the array elements.
 */
public final class SortedArray<E extends Comparable<E>> implements Iterable<E> {

    private E[] mData;
    private int mSize;

    @SuppressWarnings("unchecked")
    public SortedArray(int initialCapacity) {
        if (initialCapacity < 0) throw new IllegalArgumentException();
        mData = (E[]) new Comparable[initialCapacity];
    }

    private void ensureCapacity() {
        if (mSize == mData.length) mData = Arrays.copyOf(mData, mData.length * 3 / 2 + 1);
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
            var msg = "Index " + index + " out of bounds for size " + mSize + ".";
            throw new IndexOutOfBoundsException(msg);
        }
        return mData[index];
    }

    @Nullable
    public E retrieve(E val) {
        int pos = indexOf(val);
        return pos < 0 ? null : mData[pos];
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

    public int size() {
        return mSize;
    }

    @Override @NonNull
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
