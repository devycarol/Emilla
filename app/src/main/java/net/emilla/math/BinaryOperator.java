package net.emilla.math;

import androidx.annotation.Nullable;

import net.emilla.math.CalcToken.InfixToken;

enum BinaryOperator implements InfixToken {
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
    };

    public abstract double apply(double a, double b);

    @Nullable
    public static final BinaryOperator LPAREN = null;

    public final int precedence;
    public final boolean rightAssociative;

    BinaryOperator(int precedence, boolean rightAssociative) {
        this.precedence = precedence;
        this.rightAssociative = rightAssociative;
    }

    public static BinaryOperator of(char token) {
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
