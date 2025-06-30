package net.emilla.struct

class WrapperStruct<E>(private val value: E) : IndexedStruct<E> {

    override fun get(index: Int): E {
        if (index == 0) return value
        throw IndexOutOfBoundsException("Index $index out of range for size 1.")
    }

    override fun size() = 1
    override fun isEmpty() = false
}