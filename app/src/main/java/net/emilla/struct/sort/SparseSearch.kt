package net.emilla.struct.sort

internal class SparseSearch<E : Searchable<E>>(
    search: String,
    private val containsElements: SearchableArray<E>.SparseWindow
) : SearchResult<E>(search) {

    override val size = containsElements.size()

    init {
        require(size > 0)
    }

    override fun get(index: Int): E = containsElements[index]
    override fun isEmpty() = false

    override fun narrow(prefixedSearch: String): SearchResult<E> {
        val contains: SearchableArray<E>.SparseWindow =
            containsElements.elementsContaining(prefixedSearch)
                ?: return EmptyFilter(prefixedSearch)
        return SparseSearch(prefixedSearch, contains)
    }

    override fun onePreferredMatch() = size == 1
}
