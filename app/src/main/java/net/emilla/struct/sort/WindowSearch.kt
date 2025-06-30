package net.emilla.struct.sort

import androidx.recyclerview.widget.RecyclerView
import net.emilla.view.notifyFrontPadded
import net.emilla.view.notifyItemSpanInserted

internal open class WindowSearch<E : Searchable<E>>(
    search: String,
    private val prefixedWindow: SearchableArray<E>.Window
) : SearchResult<E>(search) {

    override val size = prefixedWindow.size()

    init {
        require(size > 0)
    }

    final override fun get(index: Int): E = prefixedWindow[index]
    final override fun isEmpty() = false

    fun window(): IndexWindow = prefixedWindow.window

    override fun narrow(prefixedSearch: String): SearchResult<E> {
        val prefixed: SearchableArray<E>.Window = prefixedWindow.prefixedBy(prefixedSearch)
            ?: return EmptyFilter(prefixedSearch)
        return WindowSearch(prefixedSearch, prefixed)
    }

    override fun onePreferredMatch() = size == 1

    override fun RecyclerView.Adapter<*>.updateToUnfilter(newFilter: Unfilter<E>) {
        val oldWindow: IndexWindow = prefixedWindow.window
        notifyFrontPadded(oldWindow.start)
        notifyItemSpanInserted(oldWindow.end, newFilter.size())
    }
}
