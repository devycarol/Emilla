@file:JvmName("Chars")

package net.emilla.util

fun Char.notSpace() = !isWhitespace()

fun Char.isNonLineSpace() = when (this) {
    '\n', '\r' -> false
    else -> isWhitespace()
}

@JvmName("differentLetters")
fun Char.differentLetter(c: Char): Boolean {
    return compareToIgnoreCase(c) != 0
}

@JvmName("compareIgnoreCase")
fun Char.compareToIgnoreCase(c: Char): Int {
    if (this != c && uppercaseChar() != c.uppercaseChar()) {
        val a = lowercaseChar()
        val b = c.lowercaseChar()
        if (a != b) return a - b
    }

    return 0
}

fun Char.isSignOrDigit() = isDigit() || isSign()
fun Char.isSignOrNumberChar() = isNumberChar() || isSign()

fun Char.isDigit() = this in '0'..'9'

fun Char.isNumberChar() = this == '.' || this in '0'..'9'

fun Char.isSign() = when (this) {
    '+', '-' -> true
    else -> false
}
