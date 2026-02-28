package net.emilla.math;

import java.util.function.UnaryOperator;

sealed interface CalcSign<T>
    extends UnaryOperator<T>
    permits ArithmeticSign, BitwiseSign
{
    boolean isPostfix();
    boolean isIdempotent();
}
