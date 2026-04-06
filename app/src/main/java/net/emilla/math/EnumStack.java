package net.emilla.math;

import androidx.annotation.Nullable;

import java.util.function.IntFunction;

final class EnumStack<O extends Enum<O>> {
    private static final int NULL = -1;

    private final byte[] mArray;
    private final IntFunction<O> mValues;
    private int mSize = 0;

    EnumStack(int capacity, IntFunction<O> values) {
        mArray = new byte[capacity];
        mValues = values;
    }

    public void push(@Nullable O item) {
        mArray[mSize] = item != null
            ? (byte) item.ordinal()
            : NULL
        ;
        ++mSize;
    }

    @Nullable
    public O peek() {
        if (isEmpty()) {
            throw Maths.malformedExpression();
        }

        return item(mArray[mSize - 1]);
    }

    @Nullable
    public O pop() {
        if (isEmpty()) {
            throw Maths.malformedExpression();
        }

        --mSize;
        return item(mArray[mSize]);
    }

    @Nullable
    private O item(byte id) {
        return id == NULL
            ? null
            : mValues.apply(id)
        ;
    }

    private boolean isEmpty() {
        return mSize == 0;
    }

    public boolean notEmpty() {
        return mSize != 0;
    }
}
