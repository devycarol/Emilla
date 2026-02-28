package net.emilla.math;

import androidx.annotation.StringRes;

public enum BitwiseCalculator {;
    public static long compute(String expression, @StringRes int errorTitle) {
        int len = expression.length();
        var operators = new EnumStack<BitwiseOperator>(len, BitwiseOperator::of, errorTitle);
        var result = new CalcStack<Long, BitwiseOperator, BitwiseSign>(
            len,
            BitwiseSign::of,
            Long[]::new,
            errorTitle
        );

        try {
            new BitwiseTokens(expression, errorTitle).forEachRemaining(
                (BitwiseToken token) -> {
                    switch (token) {
                    case BitwiseOperator op -> result.applyOperator(op, operators);
                    case BitwiseSign op -> result.applySign(op);
                    case LParen __ -> {
                        result.applyLParen();
                        operators.push(null);
                    }
                    case RParen __ -> result.applyRParen(operators);
                    case IntegerNumber number -> result.push(number.get());
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
