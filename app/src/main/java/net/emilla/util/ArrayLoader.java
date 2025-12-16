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

    private static <E> E[] concat(E[] a, E[] b, int bLimit) {
        int previousEnd = a.length;
        E[] concat = Arrays.copyOf(a, previousEnd + bLimit);
        System.arraycopy(b, 0, concat, previousEnd, bLimit);

        return concat;
    }

    @Nullable
    public static String join(ArrayLoader<String> loader, char delimiter) {
        int size = loader.mSize;
        if (size == 0) {
            return null;
        }

        String[] array = loader.mArray;
        var sb = new StringBuilder(array[0]);

        for (int i = 1; i < size; ++i) {
            sb.append(delimiter).append(array[i]);
        }

        return sb.toString();
    }

}
