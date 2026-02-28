package net.emilla.math;

enum ArithmeticSign implements CalcSign<Double>, ArithmeticToken {
    POSITIVE(false) {
        @Override
        public Double apply(Double n) {
            return n;
        }
    },
    NEGATIVE(false) {
        @Override
        public Double apply(Double n) {
            return -n;
        }
    },
    PERCENT(true) {
        @Override
        public Double apply(Double n) {
            return n / 100.0;
        }
    },
    FACTORIAL(true) {
        @Override
        public Double apply(Double n) {
            return Maths.factorial(n);
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
            case '+' -> POSITIVE;
            case '-' -> NEGATIVE;
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
        return this == POSITIVE;
    }
}
