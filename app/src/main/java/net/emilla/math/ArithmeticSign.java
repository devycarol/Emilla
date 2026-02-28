package net.emilla.math;

import androidx.annotation.Nullable;

import net.emilla.math.CalcToken.InfixToken;

enum ArithmeticSign implements InfixToken {
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
    },
;
    @Nullable
    public static final ArithmeticSign LPAREN = null;
    private static final ArithmeticSign[] sValues = values();

    public final boolean postfix;

    ArithmeticSign(boolean postfix) {
        this.postfix = postfix;
    }

    public abstract double apply(double n);

    public static ArithmeticSign of(int ordinal) {
        return sValues[ordinal];
    }

    public static ArithmeticSign of(char token) {
        return switch (token) {
            case '+' -> POSITIVE;
            case '-' -> NEGATIVE;
            case '%' -> PERCENT;
            case '!' -> FACTORIAL;
            default -> throw new IllegalArgumentException("Invalid unary operator");
        };
    }
}
