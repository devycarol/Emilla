package net.emilla.math;

import androidx.annotation.StringRes;

import java.math.BigDecimal;

public enum Calculator {;
    public static BigDecimal compute(String expression, @StringRes int errorTitle) {
        int len = expression.length();
        var operators = new EnumStack<ArithmeticOperator>(len,  ArithmeticOperator::of, errorTitle);
        var result = new CalcStack<BigDecimal, ArithmeticOperator, ArithmeticSign>(
            len,
            ArithmeticSign::of,
            BigDecimal[]::new,
            errorTitle
        );

        try {
            new ArithmeticTokens(expression, errorTitle).forEachRemaining((ArithmeticToken token) -> {
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
        } catch (ArithmeticException e) {
            throw Maths.undefined(errorTitle);
        }

        return result.value();
    }

}
