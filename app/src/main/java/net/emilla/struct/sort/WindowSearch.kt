package net.emilla.struct.sort

internal open class WindowSearch<E : Searchable<E>>(
    search: String,
    private val prefixedWindow: SearchableArray<E>.Window
) : SearchResult<E>(search) {

    private val size = prefixedWindow.size()

    init {
        require(size > 0)
    }

    final override fun get(index: Int): E = prefixedWindow.get(index)
    final override fun size() = size
    final override fun isEmpty() = false

    override fun narrow(prefixedSearch: String): SearchResult<E> {
        val prefixed: SearchableArray<E>.Window = prefixedWindow.prefixedBy(prefixedSearch)
            ?: return EmptyFilter(prefixedSearch)
        return WindowSearch(prefixedSearch, prefixed)
    }
}
