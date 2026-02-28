package net.emilla.math;

import androidx.annotation.Nullable;

import net.emilla.math.CalcToken.InfixToken;

enum ArithmeticOperator implements InfixToken {
    PLUS(1, false) {
        @Override
        public double apply(double a, double b) {
            return a + b;
        }
    },
    MINUS(1, false) {
        @Override
        public double apply(double a, double b) {
            return a - b;
        }
    },
    TIMES(2, false) {
        @Override
        public double apply(double a, double b) {
            return a * b;
        }
    },
    DIV(2, false) {
        @Override
        public double apply(double a, double b) {
            return a / b;
        }
    },
    POW(3, true) {
        @Override
        public double apply(double a, double b) {
            return Math.pow(a, b);
        }
    },
;
    @Nullable
    public static final ArithmeticOperator LPAREN = null;
    private static final ArithmeticOperator[] sValues = values();

    public final int precedence;
    public final boolean rightAssociative;

    ArithmeticOperator(int precedence, boolean rightAssociative) {
        this.precedence = precedence;
        this.rightAssociative = rightAssociative;
    }

    public abstract double apply(double a, double b);

    public static ArithmeticOperator of(int ordinal) {
        return sValues[ordinal];
    }

    public static ArithmeticOperator of(char token) {
        return switch (token) {
            case '+' -> PLUS;
            case '-' -> MINUS;
            case '*' -> TIMES;
            case '/' -> DIV;
            case '^' -> POW;
            default -> throw new IllegalArgumentException("Invalid binary operator");
        };
    }
}
