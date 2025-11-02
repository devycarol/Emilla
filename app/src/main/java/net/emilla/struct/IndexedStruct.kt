package net.emilla.struct

interface IndexedStruct<E> : RandomAccess {
    operator fun get(index: Int): E
    fun size(): Int
    fun isEmpty(): Boolean
}
