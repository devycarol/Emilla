package net.emilla.struct.sort

internal class ExactWindowSearch<E : Searchable<E>>(
    search: String,
    window: SearchableArray<E>.Window
) : WindowSearch<E>(search, window) {

    override fun narrow(prefixedSearch: String) = EmptyFilter<E>(prefixedSearch)
}