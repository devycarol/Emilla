package net.emilla.math;

/*internal*/ sealed interface BitwiseToken extends CalcToken
        permits BitwiseCalculator.BitwiseOperator, CalcToken.LParen, CalcToken.RParen, IntegerNumber {
}
