package net.emilla.math

import net.emilla.math.CalcToken.InfixToken
import kotlin.math.pow

internal enum class BinaryOperator(
    @JvmField val precedence: Int,
    @JvmField val rightAssociative: Boolean
) : InfixToken {
    PLUS(1, false) {
        override fun Double.apply(n: Double) = this + n
    },
    MINUS(1, false) {
        override fun Double.apply(n: Double) = this - n
    },
    TIMES(2, false) {
        override fun Double.apply(n: Double) = this * n
    },
    DIV(2, false) {
        override fun Double.apply(n: Double) = this / n
    },
    POW(3, true) {
        override fun Double.apply(n: Double) = pow(n)
    };

    abstract fun Double.apply(n: Double): Double

    companion object {
        @JvmField
        val LPAREN: BinaryOperator? = null

        @JvmStatic
        fun of(token: Char) = when (token) {
            // todo: natural language like "add", "to the power of", ..
            '+' -> PLUS
            '-' -> MINUS
            '*' -> TIMES
            '/' -> DIV
            '^' -> POW
            else -> throw IllegalArgumentException()
        }
    }
}
