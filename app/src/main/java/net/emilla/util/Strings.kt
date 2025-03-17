@file:JvmName("Strings")

package net.emilla.util

fun String.trimLeading(): String {
    val index = indexOfNonSpace()
    return if (index > 0) substring(index) else this
}

fun String.indexOfNonSpace(): Int {
    if (isEmpty() || !this[0].isWhitespace()) return 0

    var index = 0
    do if (++index == length) return length
    while (this[index].isWhitespace())

    return index
}

fun CharArray.indexOfNonSpace(): Int {
    if (isEmpty() || !this[0].isWhitespace()) return 0

    var index = 0
    do if (++index == size) return size
    while (this[index].isWhitespace())

    return index
}

fun Char.repeat(count: Int): String {
    return String(CharArray(count) { this })
}
