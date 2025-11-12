package net.emilla.sort;

import net.emilla.struct.IndexedStruct;

import java.util.Arrays;
import java.util.stream.Stream;

public final class ArrayWindow<E> implements IndexedStruct<E> {

    public static <E> ArrayWindow<E> closed(E[] array, int index) {
        return new ArrayWindow<E>(array, index, index);
    }

    /*internal*/ final E[] mArray;
    /*internal*/ final int mStart;
    /*internal*/ final int mEnd;

    /*internal*/ ArrayWindow(E[] array, int start, int end) {
        mArray = array;
        mStart = start;
        mEnd = end;
    }

    @Override
    public E get(int index) {
        if (index < 0) {
            throw iob(index);
        }

        index += mStart;
        if (index >= mEnd) {
            throw iob(index);
        }

        return mArray[index];
    }

    @Override
    public int size() {
        return mEnd - mStart;
    }

    @Override
    public boolean isEmpty() {
        return mStart == mEnd;
    }

    @Override
    public Stream<E> stream() {
        return Arrays.stream(mArray, mStart, mEnd);
    }

    private static IndexOutOfBoundsException iob(int index) {
        return new IndexOutOfBoundsException("Index out of range: " + index);
    }

}
