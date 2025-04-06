package net.emilla.math

import net.emilla.math.CalcToken.BitwiseToken

internal enum class BitwiseOperator(@JvmField val precedence: Int) : BitwiseToken {
    OR(-3) {
        override fun Long.apply(n: Long) = this or n
    },
    XOR(-2) {
        override fun Long.apply(n: Long) = this xor n
    },
    AND(-1) {
        override fun Long.apply(n: Long) = this and n
    },
    SHL(0) {
        override fun Long.apply(n: Long) = this shl n.toInt()
    },
    SHR(0) {
        override fun Long.apply(n: Long) = this shr n.toInt()
    },
    USHR(0) {
        override fun Long.apply(n: Long) = this ushr n.toInt()
    },
    PLUS(1) {
        override fun Long.apply(n: Long) = this + n
    },
    MINUS(1) {
        override fun Long.apply(n: Long) = this - n
    },
    TIMES(2) {
        override fun Long.apply(n: Long) = this * n
    },
    DIV(2) {
        override fun Long.apply(n: Long) = this / n
    },
    MOD(2) {
        override fun Long.apply(n: Long) = this % n
    };

    @JvmField
    val rightAssociative: Boolean = false

    abstract fun Long.apply(n: Long): Long

    companion object {
        @JvmField
        val LPAREN: BitwiseOperator? = null

        @JvmStatic
        fun of(token: String) = when (token) {
            // todo: natural language like "add", "to the power of", ..
            "|" -> OR
            "^" -> XOR
            "&" -> AND
            "<<" -> SHL
            ">>" -> SHR
            ">>>" -> USHR
            "+" -> PLUS
            "-" -> MINUS
            "*" -> TIMES
            "/" -> DIV
            "%" -> MOD
            else -> throw IllegalArgumentException()
        }
    }
}
