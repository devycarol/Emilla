package net.emilla.struct.sort

import net.emilla.util.compareToIgnoreCase

class PrefixSearcher<T : Searchable<T>>(prefix: String) : Comparable<T> {
    private val prefChars = prefix.toCharArray()
    private val prefLen = prefChars.size

    override fun compareTo(that: T): Int {
        val ordinal = that.ordinal()
        val len = ordinal.length
        if (prefLen > len) return prefLen - len

        val chars = ordinal.toCharArray()
        for (i in 0 until prefLen) {
            val cmp = prefChars[i].compareToIgnoreCase(chars[i])
            if (cmp != 0) return cmp
        }

        return 0
    }
}
