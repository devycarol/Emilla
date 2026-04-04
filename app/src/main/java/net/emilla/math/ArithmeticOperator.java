package net.emilla.math;

import java.math.BigDecimal;
import java.math.MathContext;

enum ArithmeticOperator implements CalcOperator<BigDecimal>, ArithmeticToken {
    ADD(1) {
        @Override
        public BigDecimal apply(BigDecimal a, BigDecimal b) {
            return a.add(b);
        }
    },
    SUBTRACT(1) {
        @Override
        public BigDecimal apply(BigDecimal a, BigDecimal b) {
            return a.subtract(b);
        }
    },
    MULTIPLY(2) {
        @Override
        public BigDecimal apply(BigDecimal a, BigDecimal b) {
            return a.multiply(b);
        }
    },
    DIVIDE(2) {
        @Override
        public BigDecimal apply(BigDecimal a, BigDecimal b) {
            return a.divide(b, MathContext.DECIMAL128);
        }
    },
    STUPID_POW(3) {
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
            case '+' -> ADD;
            case '-' -> SUBTRACT;
            case '*' -> MULTIPLY;
            case '/' -> DIVIDE;
            case '^' -> STUPID_POW;
            default -> throw new IllegalArgumentException("Invalid binary operator");
        };
    }

    @Override
    public final int precedence() {
        return mPrecedence;
    }

    @Override
    public final boolean isRightAssociative() {
        return this == STUPID_POW;
    }
}
