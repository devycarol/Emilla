package net.emilla.math;

sealed interface CalcToken {
    sealed interface InfixToken
        extends CalcToken
        permits ArithmeticOperator, ArithmeticSign, LParen, RParen, FloatingPointNumber
    {}

    sealed interface BitwiseToken
        extends CalcToken
        permits BitwiseOperator, BitwiseSign, LParen, RParen, IntegerNumber
    {}

    enum LParen implements InfixToken, BitwiseToken {
        INSTANCE,
    }

    enum RParen implements InfixToken, BitwiseToken {
        INSTANCE,
    }
}
