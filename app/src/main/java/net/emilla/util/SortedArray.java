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

    public E get(int index) {
        if (index >= mSize) throw new IndexOutOfBoundsException();
        return mData[index];
    }

    @Nullable
    public E retrieve(E val) {
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
        int lo = 0;
        int hi = mSize - 1;

        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            Comparable<E> midVal = mData[mid];
            int cmp = midVal.compareTo(val);

            if (cmp < 0) lo = mid + 1;
            else if (cmp > 0) hi = mid - 1;
            else return mid; // key found.
        }

        return -(lo + 1); // key not found.
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
                E e = mData[pos];
                pos++;
                return e;
            }
        };
    }
}
