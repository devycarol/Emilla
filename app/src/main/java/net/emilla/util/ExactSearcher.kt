package net.emilla.util

class ExactSearcher<T : Searchable<T>>(private val search: String) : Comparable<T> {
    override fun compareTo(that: T): Int {
        return search.compareTo(that.ordinal(), ignoreCase = true)
    }
}
