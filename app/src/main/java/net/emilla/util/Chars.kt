@file:JvmName("Chars")

package net.emilla.util

fun Char.notSpace() = !isWhitespace()

fun Char.isNonLineSpace() = when (this) {
    '\n', '\r' -> false
    else -> isWhitespace()
}

fun Char.isSignOrDigit() = isDigit() || isSign()
fun Char.isSignOrNumberChar() = isNumberChar() || isSign()

fun Char.isDigit() = this in '0'..'9'

fun Char.isNumberChar() = this == '.' || this in '0'..'9'

fun Char.isSign() = when (this) {
    '+', '-' -> true
    else -> false
}
