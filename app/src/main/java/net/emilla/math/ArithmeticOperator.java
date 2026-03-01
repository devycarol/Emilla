package net.emilla.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

enum ArithmeticOperator implements CalcOperator<BigDecimal>, ArithmeticToken {
    PLUS(1) {
        @Override
        public BigDecimal apply(BigDecimal a, BigDecimal b) {
            return a.add(b);
        }
    },
    MINUS(1) {
        @Override
        public BigDecimal apply(BigDecimal a, BigDecimal b) {
            return a.subtract(b);
        }
    },
    TIMES(2) {
        @Override
        public BigDecimal apply(BigDecimal a, BigDecimal b) {
            return a.multiply(b);
        }
    },
    DIV(2) {
        @Override
        public BigDecimal apply(BigDecimal a, BigDecimal b) {
            return a.divide(b, RoundingMode.HALF_EVEN);
        }
    },
    POW(3) {
        @Override
        public BigDecimal apply(BigDecimal a, BigDecimal b) {
            return a.pow(Maths.exactInt(b.toBigIntegerExact()));
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
