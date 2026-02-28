package net.emilla.math;

import java.util.function.BinaryOperator;

sealed interface CalcOperator<T>
    extends BinaryOperator<T>
    permits ArithmeticOperator, BitwiseOperator
{
    int precedence();
    boolean isRightAssociative();
}
