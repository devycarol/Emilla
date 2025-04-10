package net.emilla.struct.sort

class ExactSearcher<T : Searchable<T>>(private val search: String) : Comparable<T> {
    override fun compareTo(that: T): Int {
        return search.compareTo(that.ordinal(), ignoreCase = true)
    }
}
