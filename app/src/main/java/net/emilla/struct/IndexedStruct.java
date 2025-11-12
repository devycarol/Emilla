package net.emilla.struct;

import java.util.Iterator;
import java.util.RandomAccess;
import java.util.stream.Stream;

public interface IndexedStruct<E> extends Iterable<E>, RandomAccess {

    E get(int index);
    int size();
    boolean isEmpty();
    Stream<E> stream();

    @Override
    default Iterator<E> iterator() {
        return stream().iterator();
    }

}
