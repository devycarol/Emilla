package net.emilla.struct.sort

import net.emilla.struct.IndexedStruct

interface FilterResult<E> : IndexedStruct<E> {
    fun onePreferredMatch(): Boolean
}
