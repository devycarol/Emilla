package net.emilla.util

object Strings {
    @JvmStatic
    fun String.trimLeading(): String {
        val index = indexOfNonSpace()
        if (index > 0) return substring(index)
        return this
    }

    @JvmStatic
    fun String.indexOfNonSpace(): Int {
        if (isEmpty() || !get(0).isWhitespace()) return 0

        var index = 0
        do if (++index == length) return length
        while (get(index).isWhitespace())

        return index
    }
}
