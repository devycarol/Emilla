package net.emilla.struct.sort

import androidx.recyclerview.widget.RecyclerView
import net.emilla.struct.IndexedStruct

sealed interface FilterResult<E : Searchable<E>> : IndexedStruct<E> {
    /**
     * Derives a narrower filter result for a search that is prefixed by this result's search term.
     * This enables more efficient algorithms while the result should still be the same as calling
     * [SearchableArray.filter] directly. The result will probably be incorrect if [prefixedSearch]
     * isn't prefixed by the search term that generated this result!
     *
     * @param prefixedSearch search query for which this filter result's search term is a
     * case-insensitive prefix.
     * @return the appropriate filter result for the more specific search term.
     */
    fun narrow(prefixedSearch: String): SearchResult<E>
    fun update(adapter: RecyclerView.Adapter<*>, newFilter: FilterResult<E>)
    fun onePreferredMatch(): Boolean
}
