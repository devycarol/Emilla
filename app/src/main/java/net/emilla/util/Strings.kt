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

fun String.containsIgnoreCase(other: String): Boolean {
    if (other.isEmpty()) return true
    if (isEmpty()) return false

    val first = other[0]
    val max = length - other.length
    val trgLast = other.length - 1

    var i = 0
    while (i <= max) {
        // look for first char.
        if (this[i].differentLetter(first)) {
            do ++i
            while (i <= max && this[i].differentLetter(first))

            if (i > max) return false
        }

        // found first character, now look at the rest of target.
        ++i
        if (regionMatches(i, other, 1, trgLast)) return true
    }

    return false
}

fun Char.repeat(count: Int): String = String(CharArray(count) { this })
fun CharArray.substring(start: Int = 0) = substring(start, size)
fun CharArray.substring(start: Int = 0, end: Int) = String(copyOfRange(start, end))
