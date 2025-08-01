package net.emilla.struct.sort;

import net.emilla.struct.IndexedStruct;

public final class Unfilter<E> implements FilterResult<E> {

    private final IndexedStruct<E> mData;

    public Unfilter(IndexedStruct<E> data) {
        mData = data;
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
    public boolean onePreferredMatch() {
        return size() == 1;
    }
}
