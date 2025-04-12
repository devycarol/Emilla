package net.emilla.struct.sort

internal class SparseSearch<E : Searchable<E>>(
    search: String,
    private val containsElements: SearchableArray<E>
) : SearchResult<E>(search) {

    private val size = containsElements.size()

    init {
        require(size > 0)
    }

    override fun get(index: Int): E = containsElements.get(index)
    override fun size() = size
    override fun isEmpty() = false

    override fun narrow(prefixedSearch: String): SearchResult<E> {
        val contains: SearchableArray<E> = containsElements.elementsContaining(prefixedSearch)
            ?: return EmptyFilter(prefixedSearch)
        return SparseSearch(prefixedSearch, contains)
    }
}
