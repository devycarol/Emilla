package net.emilla.struct.sort

import net.emilla.util.containsIgnoreCase

abstract class Searchable<T : Searchable<T>> : Comparable<T> {
    abstract fun ordinal(): String

    override fun compareTo(other: T) = compareTo(other.ordinal())
    fun compareTo(search: String) = ordinal().compareTo(search, ignoreCase = true)

    fun ordinalIs(search: String) = ordinal().equals(search, ignoreCase = true)
    fun ordinalContains(search: String) = ordinal().containsIgnoreCase(search)
}
