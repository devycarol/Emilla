package net.emilla.math;

import net.emilla.math.CalcToken.LParen;
import net.emilla.math.CalcToken.RParen;

/*internal*/ sealed interface InfixToken extends CalcToken
        permits LParen, RParen, BinaryOperator, UnaryOperator, FloatingPointNumber {
}
