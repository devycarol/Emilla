@file:JvmName("Strings")

package net.emilla.util

fun String?.emptyIfNull(): String = this ?: ""

fun CharArray.indexOfNonSpace(): Int {
    if (isEmpty() || !this[0].isWhitespace()) return 0

    var index = 0
    do if (++index == size) return size
    while (this[index].isWhitespace())

    return index
}

fun Char.repeat(count: Int) = String(CharArray(count) { this })
fun CharArray.substring(start: Int = 0): String = substring(start, size)
fun CharArray.substring(start: Int = 0, end: Int) = String(copyOfRange(start, end))

fun String.stripNonDigits(): String = stripNonMatching(Character::isDigit)
fun String.stripNonNumbers(): String = stripNonMatching(Chars::isNumberChar)
fun String.stripSpaces(): String = stripMatching(Character::isWhitespace)

private inline fun String.stripMatching(condition: (Char) -> Boolean): String
    = stripNonMatching { !condition(it) }

private inline fun String.stripNonMatching(filter: (Char) -> Boolean): String {
    val chars = this.toCharArray()
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
