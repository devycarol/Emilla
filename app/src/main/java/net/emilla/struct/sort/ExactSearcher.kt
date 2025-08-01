package net.emilla.struct.sort

class ExactSearcher<T : Searchable<T>>(private val search: String) : Comparable<T> {
    override fun compareTo(other: T): Int {
        return search.compareTo(other.ordinal(), ignoreCase = true)
    }
}
