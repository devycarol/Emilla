@file:JvmName("Strings")

package net.emilla.util

fun String.trimLeading(): String {
    val index = indexOfNonSpace()
    return if (index > 0) substring(index) else this
}

fun String.indexOfNonSpace(): Int {
    if (isEmpty() || !get(0).isWhitespace()) return 0

    var index = 0
    do if (++index == length) return length
    while (get(index).isWhitespace())

    return index
}
