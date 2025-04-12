package net.emilla.struct.sort

import net.emilla.struct.IndexedStruct

abstract class SearchResult<E : Searchable<E>>(
    private val search: String
) : Searchable<SearchResult<E>>(), IndexedStruct<E> {

    override fun ordinal() = search
    /**
     * Derives a narrower filter result for a search that is prefixed by this result's search term.
     * This enables more efficient algorithms while the result should still be the same as calling
     * [SearchableArray.filter] directly. The result will probably be incorrect if [prefixedSearch]
     * isn't prefixed by [search].
     *
     * @param prefixedSearch search query for which this filter result's search term is a
     * case-insensitive prefix.
     * @return the appropriate filter result for the more specific search term.
     */
    abstract fun narrow(prefixedSearch: String): SearchResult<E>
}
