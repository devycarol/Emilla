package net.emilla.math;

import androidx.annotation.Nullable;

import net.emilla.math.CalcToken.InfixToken;

enum UnaryOperator implements InfixToken {
    POSITIVE(false) {
        @Override
        public double apply(double n) {
            return n;
        }
    },
    NEGATIVE(false) {
        @Override
        public double apply(double n) {
            return -n;
        }
    },
    PERCENT(true) {
        @Override
        public double apply(double n) {
            return n / 100.0;
        }
    },
    FACTORIAL(true) {
        @Override
        public double apply(double n) {
            return Maths.factorial(n);
        }
    };

    public abstract double apply(double n);

    public final boolean postfix;

    UnaryOperator(boolean postfix) {
        this.postfix = postfix;
    }

    @Nullable
    public static final UnaryOperator LPAREN = null;

    public static UnaryOperator of(char token) {
        return switch (token) {
            case '+' -> POSITIVE;
            case '-' -> NEGATIVE;
            case '%' -> PERCENT;
            case '!' -> FACTORIAL;
            default -> throw new IllegalArgumentException("Invalid unary operator");
        };
    }

}
