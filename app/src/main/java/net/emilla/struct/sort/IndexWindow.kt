package net.emilla.struct.sort

@ConsistentCopyVisibility
data class IndexWindow internal constructor(@JvmField val start: Int, @JvmField val last: Int) {
    @JvmField
    val end = last + 1

    fun size() = end - start
}
