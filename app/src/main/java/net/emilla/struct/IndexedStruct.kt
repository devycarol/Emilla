package net.emilla.struct

import java.util.stream.Stream

interface IndexedStruct<E> : Iterable<E>, RandomAccess {
    operator fun get(index: Int): E
    fun size(): Int
    fun isEmpty(): Boolean
    fun stream(): Stream<E>
}
