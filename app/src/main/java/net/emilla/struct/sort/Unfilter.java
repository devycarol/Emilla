package net.emilla.struct.sort;

import net.emilla.struct.IndexedStruct;

import java.util.Iterator;
import java.util.stream.Stream;

public final class Unfilter<E> implements FilterResult<E> {

    private final IndexedStruct<E> mData;

    public Unfilter(IndexedStruct<E> data) {
        mData = data;
    }

    @Override
    public boolean onePreferredMatch() {
        return size() == 1;
    }

    @Override
    public E get(int index) {
        return mData.get(index);
    }

    @Override
    public int size() {
        return mData.size();
    }

    @Override
    public boolean isEmpty() {
        return mData.isEmpty();
    }

    @Override
    public Stream<E> stream() {
        return mData.stream();
    }

    @Override
    public Iterator<E> iterator() {
        return mData.iterator();
    }

}
