package net.emilla.math;

sealed public interface BitwiseToken
    permits BitwiseOperator, BitwiseSign, IntegerNumber, LParen, RParen {
}
