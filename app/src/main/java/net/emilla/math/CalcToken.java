package net.emilla.math;

sealed interface CalcToken {
    sealed interface InfixToken
        extends CalcToken
        permits BinaryOperator, LParen, RParen, FloatingPointNumber, UnaryOperator
    {}

    sealed interface BitwiseToken
        extends CalcToken
        permits BitwiseOperator, BitwiseSign, LParen, RParen, IntegerNumber
    {}

    enum LParen implements InfixToken, BitwiseToken {
        INSTANCE
    }

    enum RParen implements InfixToken, BitwiseToken {
        INSTANCE
    }
}
