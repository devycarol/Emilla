package net.emilla.math

import net.emilla.math.CalcToken.InfixToken

internal enum class UnaryOperator(@JvmField val postfix: Boolean) : InfixToken {
    POSITIVE(false) {
        override fun Double.apply() = this
    },
    NEGATIVE(false) {
        override fun Double.apply() = -this
    },
    PERCENT(true) {
        override fun Double.apply() = this / 100.0
    },
    FACTORIAL(true) {
        override fun Double.apply() = factorial()
    };

    abstract fun Double.apply(): Double

    companion object {
        @JvmField
        val LPAREN: UnaryOperator? = null

        @JvmStatic
        fun of(token: Char) = when (token) {
            // todo: natural language like "positive", "factorial", ..
            '+' -> POSITIVE
            '-' -> NEGATIVE
            '%' -> PERCENT
            '!' -> FACTORIAL
            else -> throw IllegalArgumentException()
        }
    }
}
