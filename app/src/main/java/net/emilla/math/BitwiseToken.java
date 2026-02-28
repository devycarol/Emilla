package net.emilla.math;

sealed public interface BitwiseToken
    extends CalcToken
    permits BitwiseOperator, BitwiseSign, IntegerNumber, LParen, RParen {
}
