package net.emilla.util

object Strings {
    @JvmStatic
    fun String.nullIfEmpty(): String? {
        return if (isEmpty()) null else this
    }

    @JvmStatic
    fun String.subTrimToNull(index: Int): String? {
        return substring(index).trim().nullIfEmpty()
    }
}
