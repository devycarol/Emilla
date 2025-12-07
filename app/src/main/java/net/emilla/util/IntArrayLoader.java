package net.emilla.util;

import java.util.Arrays;

public final class IntArrayLoader {

    private static final int[] EMPTY_ARRAY = new int[0];

    private final int[] mArray;
    private int mSize = 0;

    public IntArrayLoader(int capacity) {
        mArray = new int[capacity];
    }

    public void add(int i) {
        mArray[mSize] = i;
        // IOB at capacity
        ++mSize;
    }

    public int[] array() {
        if (mSize == 0) {
            return EMPTY_ARRAY;
        }
        if (mSize == mArray.length) {
            return mArray;
        }
        return Arrays.copyOf(mArray, mSize);
    }

}
