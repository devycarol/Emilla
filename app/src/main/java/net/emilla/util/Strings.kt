@file:JvmName("Strings")

package net.emilla.util

fun emptyIfNull(s: String?) = s ?: ""

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
        if (Chars.differentLetters(this[i], first)) {
            do ++i
            while (i <= max && Chars.differentLetters(this[i], first))

            if (i > max) return false
        }

        // found first character, now look at the rest of target.
        ++i
        if (regionMatches(i, other, 1, trgLast)) return true
    }

    return false
}

internal fun ByteArray.count(b: Byte): Int = count { it == b }
fun Char.repeat(count: Int): String = String(CharArray(count) { this })
fun CharArray.substring(start: Int = 0): String = substring(start, size)
fun CharArray.substring(start: Int = 0, end: Int): String = String(copyOfRange(start, end))

fun String.stripNonDigits(): String = stripNonMatching(this, Character::isDigit)
fun String.stripNonNumbers(): String = stripNonMatching(this, Chars::isNumberChar)
fun String.stripSpaces(): String = stripMatching(this, Character::isWhitespace)

private inline fun stripMatching(s: String, condition: (Char) -> Boolean): String
    = stripNonMatching(s) { !condition(it) }

private inline fun stripNonMatching(s: String, filter: (Char) -> Boolean): String {
    val chars = s.toCharArray()
    var pos = 0

    for (i in 0..<chars.size) {
        if (filter(chars[i])) {
            chars[pos] = chars[i]
            ++pos
        }
    }

    return String(chars, 0, pos)
}

fun CharSequence.isOneToNDigits(n: Int): Boolean = length in 1..n && all(Character::isDigit)
