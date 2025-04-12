package net.emilla.struct.sort

internal open class EmptyFilter<E : Searchable<E>>(search: String) : SearchResult<E>(search) {
    final override fun get(index: Int) = throw UnsupportedOperationException()
    final override fun size() = 0
    final override fun isEmpty() = true
    override fun narrow(prefixedSearch: String) = EmptyFilter<E>(prefixedSearch)
}
