package net.emilla.math;

sealed public interface ArithmeticToken
    permits ArithmeticOperator, ArithmeticSign, FloatingPointNumber, LParen, RParen {
}
