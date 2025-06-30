package net.emilla.struct.sort

@ConsistentCopyVisibility
data class IndexWindow internal constructor(
    @JvmField val start: Int,
    @JvmField val last: Int
): IndexPortion {

    @JvmField
    val end = last + 1
    @JvmField
    val size = end - start

    fun isEmpty() = start == end
    fun arrayIndex(windowIndex: Int) = start + windowIndex
}
