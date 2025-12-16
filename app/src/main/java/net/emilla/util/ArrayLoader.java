package net.emilla.util;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.function.IntFunction;

public final class ArrayLoader<E> {

    private E[] mArray;
    private int mSize = 0;

    public ArrayLoader(int capacity, IntFunction<E[]> generator) {
        mArray = generator.apply(capacity);
    }

    public void add(@Nullable E e) {
        mArray[mSize] = e;
        // IOB at capacity
        ++mSize;
    }

    public void growingAdd(@Nullable E e) {
        if (mSize == mArray.length) {
            mArray = Arrays.copyOf(mArray, mSize * 3 / 2 + 1);
        }
        add(e);
    }

    public boolean notEmpty() {
        return mSize > 0;
    }

    public E[] array() {
        return mSize == mArray.length
            ? mArray
            : Arrays.copyOf(mArray, mSize);
    }

    public E[] appendedTo(E[] array) {
        return concat(array, mArray, mSize);
    }

    public static <E> E[] concat(E[] a, E[] b) {
        return concat(a, b, b.length);
    }

    private static <E> E[] concat(E[] a, E[] b, int bSize) {
        int previousEnd = a.length;
        E[] appendedTo = Arrays.copyOf(a, previousEnd + bSize);
        System.arraycopy(b, 0, appendedTo, previousEnd, bSize);

        return appendedTo;
    }

}
