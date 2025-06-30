package net.emilla.struct.sort

import androidx.recyclerview.widget.RecyclerView
import net.emilla.view.notifyItemsClamped
import net.emilla.view.notifyTrimmedUntil

internal class Unfilter<E : Searchable<E>>(private val data: SearchableArray<E>) : FilterResult<E> {
    override fun get(index: Int): E = data[index]
    override fun size() = data.size()
    override fun isEmpty() = data.isEmpty()

    override fun narrow(prefixedSearch: String): SearchResult<E> = data.filter(prefixedSearch)

    override fun update(adapter: RecyclerView.Adapter<*>, newFilter: FilterResult<E>) {
        when (newFilter) {
            is EmptyFilter<E> -> {
                adapter.notifyTrimmedUntil(size())
            }
            is WindowSearch<E> -> {
                adapter.notifyItemsClamped(newFilter.window(), size())
            }
            is SparseSearch<E> -> {
                adapter.notifyDataSetChanged()
            }
            is SectoredSearch<E> -> {
                adapter.notifyDataSetChanged()
            }
            is Unfilter<E> -> { /* nothing to do. */ }
        }
    }

    override fun onePreferredMatch() = data.size() == 1
}
