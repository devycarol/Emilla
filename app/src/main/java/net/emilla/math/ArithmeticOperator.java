package net.emilla.math;

enum ArithmeticOperator implements CalcOperator<Double>, ArithmeticToken {
    PLUS(1) {
        @Override
        public Double apply(Double a, Double b) {
            return a + b;
        }
    },
    MINUS(1) {
        @Override
        public Double apply(Double a, Double b) {
            return a - b;
        }
    },
    TIMES(2) {
        @Override
        public Double apply(Double a, Double b) {
            return a * b;
        }
    },
    DIV(2) {
        @Override
        public Double apply(Double a, Double b) {
            return a / b;
        }
    },
    POW(3) {
        @Override
        public Double apply(Double a, Double b) {
            return Math.pow(a, b);
        }
    },
;
    private static final ArithmeticOperator[] sValues = values();

    private final int mPrecedence;

    ArithmeticOperator(int precedence) {
        mPrecedence = precedence;
    }

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

    @Override
    public final int precedence() {
        return mPrecedence;
    }

    @Override
    public final boolean isRightAssociative() {
        return this == POW;
    }
}
