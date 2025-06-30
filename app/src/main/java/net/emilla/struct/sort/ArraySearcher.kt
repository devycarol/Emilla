package net.emilla.struct.sort

class ArraySearcher<E : Searchable<E>>(c: Collection<E>) {
    private val data = SearchableArray<E>(c)
    private val filterCache = SearchableArray<SearchResult<E>>(16)

    fun search(search: String?): FilterResult<E> {
        if (search == null) return Unfilter<E>(data)
        filterCache[search]?.let { return it }

        for (i in search.length - 1 downTo 1) {
            filterCache[search.substring(0, i)]?.let {
                return cache(it.narrow(search))
            }
        }

        return cache(data.filter(search))
    }

    private fun cache(filter: SearchResult<E>): SearchResult<E> {
        filterCache.add(filter)
        return filter
    }
}
