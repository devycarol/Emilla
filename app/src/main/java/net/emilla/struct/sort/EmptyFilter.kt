package net.emilla.struct.sort

import androidx.recyclerview.widget.RecyclerView
import net.emilla.view.notifyFrontPadded

internal class EmptyFilter<E : Searchable<E>>(search: String) : SearchResult<E>(search) {
    override fun get(index: Int) = throw UnsupportedOperationException()
    override val size = 0
    override fun isEmpty() = true

    override fun narrow(prefixedSearch: String) = EmptyFilter<E>(prefixedSearch)

    override fun update(adapter: RecyclerView.Adapter<*>, newFilter: FilterResult<E>) {
        adapter.notifyFrontPadded(newFilter.size())
    }

    override fun onePreferredMatch() = false
}
