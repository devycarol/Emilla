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

    public E[] array() {
        return mSize == mArray.length
            ? mArray
            : Arrays.copyOf(mArray, mSize)
        ;
    }
}
