package net.emilla.math;

import net.emilla.math.CalcToken.LParen;
import net.emilla.math.CalcToken.RParen;

/*internal*/ sealed interface BitwiseToken extends CalcToken
        permits BitwiseOperator, BitwiseSign, LParen, RParen, IntegerNumber {
}
