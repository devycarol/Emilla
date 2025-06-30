package net.emilla.struct.sort

data class SearchItem(@JvmField val label: String) : Searchable<SearchItem>() {
    override fun ordinal() = label
}
