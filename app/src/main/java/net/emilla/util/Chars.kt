@file:JvmName("Chars")

package net.emilla.util

fun Char.isNonLineSpace(): Boolean {
    return when (this) {
        '\n', '\r' -> false
        else -> isWhitespace()
    }
}
