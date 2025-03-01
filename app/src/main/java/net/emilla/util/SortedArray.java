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

    private void bumpCapacity() {
        mData = Arrays.copyOf(mData, mData.length * 3 / 2 + 1);
    }

    public void add(E val) {
        if (mSize == mData.length) bumpCapacity();

        int pos = indexFor(val);
        System.arraycopy(mData, pos, mData, pos + 1, mSize - pos);
        mData[pos] = val;

        ++mSize;
    }

    @Nullable
    public E retrieve(@NonNull E val) {
        int pos = indexOf(val);
        if (pos < 0) return null;
        return mData[pos];
    }

    private int indexFor(E val) {
        int index = indexOf(val);
        if (index < 0) index = -index - 1;
        return index;
    }

    private int indexOf(E val) {
        return Arrays.binarySearch(mData, 0, mSize, val);
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
