package net.emilla.math;

import androidx.annotation.StringRes;

import java.math.BigInteger;

public enum BitwiseCalculator {;
    public static BigInteger compute(String expression, @StringRes int errorTitle) {
        int len = expression.length();
        var operators = new EnumStack<BitwiseOperator>(len, BitwiseOperator::of, errorTitle);
        var result = new CalcStack<BigInteger, BitwiseOperator, BitwiseSign>(
            len,
            BitwiseSign::of,
            BigInteger[]::new,
            errorTitle
        );

        try {
            new BitwiseTokens(expression, errorTitle).forEachRemaining(
                (BitwiseToken token) -> {
                    switch (token) {
                    case BitwiseOperator operator -> result.applyOperator(operator, operators);
                    case BitwiseSign sign -> result.applySign(sign);
                    case LParen __ -> {
                        result.applyLParen();
                        operators.push(null);
                    }
                    case RParen __ -> result.applyRParen(operators);
                    case IntegerNumber number -> result.push(number.value());
                    }
                }
            );

            while (operators.notEmpty()) {
                BitwiseOperator pop = operators.pop();
                if (pop != null) {
                    // not left paren
                    result.squish(pop);
                } else {
                    while (operators.notEmpty()) {
                        if (operators.peek() == null) {
                            // left paren
                            operators.pop();
                        } else {
                            result.applyRParen(operators);
                        }
                    }
                }
            }

            result.applyRemainingSigns();
        } catch (ArithmeticException e) {
            throw Maths.undefined(errorTitle);
        }

        return result.value();
    }
}
