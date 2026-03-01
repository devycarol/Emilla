package net.emilla.math;

import java.math.BigDecimal;

enum ArithmeticSign implements CalcSign<BigDecimal>, ArithmeticToken {
    PLUS(false) {
        @Override
        public BigDecimal apply(BigDecimal n) {
            return n;
        }
    },
    NEGATE(false) {
        @Override
        public BigDecimal apply(BigDecimal n) {
            return n.negate();
        }
    },
    PERCENT(true) {
        @Override
        public BigDecimal apply(BigDecimal n) {
            return n.movePointLeft(2);
        }
    },
    FACTORIAL(true) {
        @Override
        public BigDecimal apply(BigDecimal n) {
            return new BigDecimal(Maths.stupidFactorial(n.toBigIntegerExact()));
        }
    },
;
    private static final ArithmeticSign[] sValues = values();

    private final boolean mIsPostfix;

    ArithmeticSign(boolean isPostfix) {
        mIsPostfix = isPostfix;
    }

    public static ArithmeticSign of(int ordinal) {
        return sValues[ordinal];
    }

    public static ArithmeticSign of(char token) {
        return switch (token) {
            case '+' -> PLUS;
            case '-' -> NEGATE;
            case '%' -> PERCENT;
            case '!' -> FACTORIAL;
            default -> throw new IllegalArgumentException("Invalid unary operator");
        };
    }

    @Override
    public final boolean isPostfix() {
        return mIsPostfix;
    }

    @Override
    public final boolean isIdempotent() {
        return this == PLUS;
    }
}
