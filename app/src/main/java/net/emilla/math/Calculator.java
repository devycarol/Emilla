package net.emilla.math;

import java.math.BigDecimal;

public enum Calculator {;
    /// @throws ArithmeticException if the result of the expression is undefined.
    /// @throws NumberFormatException if the expression is malformed or a number fails to be parsed.
    public static BigDecimal compute(String expression) {
        int len = expression.length();
        var operators = new EnumStack<ArithmeticOperator>(len,  ArithmeticOperator::of);
        var result = new CalcStack<BigDecimal, ArithmeticOperator, ArithmeticSign>(
            len,
            ArithmeticSign::of,
            BigDecimal[]::new
        );

        new ArithmeticTokens(expression).forEachRemaining((ArithmeticToken token) -> {
            switch (token) {
            case ArithmeticOperator operator -> result.applyOperator(operator, operators);
            case ArithmeticSign sign -> result.applySign(sign);
            case LParen __ -> {
                result.applyLParen();
                operators.push(null);
            }
            case RParen __ -> result.applyRParen(operators);
            case FloatingPointNumber number -> result.push(number.value());
            }
        });

        while (operators.notEmpty()) {
            ArithmeticOperator pop = operators.pop();
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
