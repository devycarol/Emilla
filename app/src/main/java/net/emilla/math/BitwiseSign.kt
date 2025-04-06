package net.emilla.math

import net.emilla.math.CalcToken.BitwiseToken

internal enum class BitwiseSign(@JvmField val postfix: Boolean) : BitwiseToken {
    POSITIVE(false) {
        override fun Long.apply() = this
    },
    NEGATIVE(false) {
        override fun Long.apply() = -this
    },
    NOT(false) {
        override fun Long.apply() = inv()
    },
    FACTORIAL(true) {
        override fun Long.apply() = factorial()
    };

    abstract fun Long.apply(): Long

    companion object {
        @JvmField
        val LPAREN: BitwiseSign? = null

        @JvmStatic
        fun of(token: Char) = when (token) {
            // todo: natural language like "positive", "factorial", ..
            '+' -> POSITIVE
            '-' -> NEGATIVE
            '~' -> NOT
            '!' -> FACTORIAL
            else -> throw IllegalArgumentException()
        }
    }
}
