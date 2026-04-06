package net.emilla.math;

import java.math.BigInteger;

public enum BitwiseCalculator {;
    /// @throws ArithmeticException if the result of the expression is undefined.
    /// @throws NumberFormatException if the expression is malformed or a number fails to be parsed.
    public static BigInteger compute(String expression) {
        int len = expression.length();
        var operators = new EnumStack<BitwiseOperator>(len, BitwiseOperator::of);
        var result = new CalcStack<BigInteger, BitwiseOperator, BitwiseSign>(
            len,
            BitwiseSign::of,
            BigInteger[]::new
        );

        new BitwiseTokens(expression).forEachRemaining(
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

        return result.value();
    }
}
