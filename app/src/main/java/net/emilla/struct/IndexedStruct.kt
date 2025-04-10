package net.emilla.struct

interface IndexedStruct<E> {
    fun get(index: Int): E
    fun size(): Int
    fun isEmpty(): Boolean
}
