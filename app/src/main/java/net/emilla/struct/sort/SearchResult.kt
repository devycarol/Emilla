package net.emilla.struct.sort

import androidx.recyclerview.widget.RecyclerView
import net.emilla.view.notifyTrimmedUntil

sealed class SearchResult<E : Searchable<E>>(
    private val search: String
) : Searchable<SearchResult<E>>(), FilterResult<E> {

    protected abstract val size: Int
    final override fun size() = size

    final override fun ordinal() = search

    override fun update(adapter: RecyclerView.Adapter<*>, newFilter: FilterResult<E>) {
        when (newFilter) {
            is EmptyFilter<E> -> {
                adapter.notifyTrimmedUntil(size)
            }
            is WindowSearch<E> -> {
                adapter.updateToWindowSearch(newFilter)
            }
            is SparseSearch<E> -> {
                adapter.updateToSparseSearch(newFilter)
            }
            is SectoredSearch<E> -> {
                adapter.updateToSectoredSearch(newFilter)
            }
            is Unfilter<E> -> {
                adapter.updateToUnfilter(newFilter)
            }
        }
    }

    internal open fun RecyclerView.Adapter<*>.updateToWindowSearch(newFilter: WindowSearch<E>) {
        notifyDataSetChanged()
    }

    internal open fun RecyclerView.Adapter<*>.updateToSparseSearch(newFilter: SparseSearch<E>) {
        notifyDataSetChanged()
    }

    internal open fun RecyclerView.Adapter<*>.updateToSectoredSearch(newFilter: SectoredSearch<E>) {
        notifyDataSetChanged()
    }

    internal open fun RecyclerView.Adapter<*>.updateToUnfilter(newFilter: Unfilter<E>) {
        notifyDataSetChanged()
    }
}
