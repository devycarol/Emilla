package net.emilla.util;

import java.util.Arrays;
import java.util.function.IntFunction;

public final class ArrayLoader<E> {

    private final E[] mArray;
    private int mSize = 0;

    public ArrayLoader(int capacity, IntFunction<E[]> generator) {
        mArray = generator.apply(capacity);
    }

    public void add(E e) {
        mArray[mSize] = e;
        // IOB at capacity
        ++mSize;
    }

    public boolean notEmpty() {
        return mSize > 0;
    }

    public E[] array() {
        if (mSize == mArray.length) {
            return mArray;
        }
        return Arrays.copyOf(mArray, mSize);
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
