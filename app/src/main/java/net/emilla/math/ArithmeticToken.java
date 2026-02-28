package net.emilla.math;

sealed public interface ArithmeticToken
    extends CalcToken
    permits ArithmeticOperator, ArithmeticSign, FloatingPointNumber, LParen, RParen {
}
