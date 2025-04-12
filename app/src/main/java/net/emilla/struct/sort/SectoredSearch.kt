package net.emilla.struct.sort

internal class SectoredSearch<E : Searchable<E>>(
    search: String,
    private val prefixedWindow: SearchableArray<E>.Window,
    private val containsElements: SearchableArray<E>
) : SearchResult<E>(search) {

    private val prefSize = prefixedWindow.size()
    private val size = prefSize + containsElements.size()

    init {
        require(prefSize > 0 && size > 1)
    }

    override fun get(index: Int): E {
        return if (index < prefSize) {
            prefixedWindow.get(index)
        } else {
            containsElements.get(index - prefSize)
        }
    }

    override fun size() = size
    override fun isEmpty() = false

    override fun narrow(prefixedSearch: String): SearchResult<E> {
        val prefixed: SearchableArray<E>.Window? = prefixedWindow.prefixedBy(prefixedSearch)
        val contains: SearchableArray<E>? = containsElements.elementsContaining(prefixedSearch)
        return if (prefixed != null && contains != null) {
            SectoredSearch(prefixedSearch, prefixed, contains)
        } else if (prefixed != null) {
            WindowSearch(prefixedSearch, prefixed)
        } else if (contains != null) {
            SparseSearch(prefixedSearch, contains)
        } else {
            EmptyFilter(prefixedSearch)
        }
    }
}
