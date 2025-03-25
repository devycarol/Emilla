package net.emilla.math;

/*internal*/ sealed interface InfixToken extends CalcToken
        permits CalcToken.LParen, CalcToken.RParen, Calculator.BinaryOperator, FloatingPointNumber {
}
