package net.emilla.sort;

import net.emilla.struct.IndexedStruct;

import java.util.Arrays;
import java.util.stream.Stream;

/*internal*/ final class ArrayWrapper<E> implements IndexedStruct<E> {

    /*internal*/ E[] mArray;

    /*internal*/ ArrayWrapper(E[] array) {
        mArray = array;
    }

    @Override
    public E get(int index) {
        return mArray[index];
    }

    @Override
    public int size() {
        return mArray.length;
    }

    @Override
    public boolean isEmpty() {
        return mArray.length == 0;
    }

    @Override
    public Stream<E> stream() {
        return Arrays.stream(mArray);
    }

}
