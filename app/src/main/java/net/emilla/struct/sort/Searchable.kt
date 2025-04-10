package net.emilla.struct.sort

import net.emilla.util.containsIgnoreCase

abstract class Searchable<T : Searchable<T>> : Comparable<T> {
    abstract fun ordinal(): String

    override fun compareTo(that: T) = compareTo(that.ordinal())
    fun compareTo(search: String) = ordinal().compareTo(search, ignoreCase = true)

    fun ordinalContains(search: String) = ordinal().containsIgnoreCase(search)
}
