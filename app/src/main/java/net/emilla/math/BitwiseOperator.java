package net.emilla.math;

import java.math.BigInteger;

enum BitwiseOperator implements CalcOperator<BigInteger>, BitwiseToken {
    OR(-3) {
        @Override
        public BigInteger apply(BigInteger a, BigInteger b) {
            return a.or(b);
        }
    },
    XOR(-2) {
        @Override
        public BigInteger apply(BigInteger a, BigInteger b) {
            return a.xor(b);
        }
    },
    AND(-1) {
        @Override
        public BigInteger apply(BigInteger a, BigInteger b) {
            return a.and(b);
        }
    },
    SHIFT_LEFT(0) {
        @Override
        public BigInteger apply(BigInteger a, BigInteger b) {
            return a.shiftLeft(Maths.exactInt(b));
        }
    },
    SHIFT_RIGHT(0) {
        @Override
        public BigInteger apply(BigInteger a, BigInteger b) {
            return a.shiftRight(Maths.exactInt(b));
        }
    },
    ADD(1) {
        @Override
        public BigInteger apply(BigInteger a, BigInteger b) {
            return a.add(b);
        }
    },
    SUBTRACT(1) {
        @Override
        public BigInteger apply(BigInteger a, BigInteger b) {
            return a.subtract(b);
        }
    },
    MULTIPLY(2) {
        @Override
        public BigInteger apply(BigInteger a, BigInteger b) {
            return a.multiply(b);
        }
    },
    DIVIDE(2) {
        @Override
        public BigInteger apply(BigInteger a, BigInteger b) {
            return a.divide(b);
        }
    },
    MOD(2) {
        @Override
        public BigInteger apply(BigInteger a, BigInteger b) {
            return a.mod(b);
        }
    },
    STUPID_POW(3) {
        @Override
        public BigInteger apply(BigInteger a, BigInteger b) {
            return a.pow(Maths.exactInt(b));
        }
    },
;
    private static final BitwiseOperator[] sValues = values();

    private final int mPrecedence;

    BitwiseOperator(int precedence) {
        mPrecedence = precedence;
    }

    public static BitwiseOperator of(int ordinal) {
        return sValues[ordinal];
    }

    public static BitwiseOperator of(char token) {
        return switch (token) {
            case '|' -> OR;
            case '^' -> XOR;
            case '&' -> AND;
            case '<' -> SHIFT_LEFT;
            case '>' -> SHIFT_RIGHT;
            case '+' -> ADD;
            case '-' -> SUBTRACT;
            case '*' -> MULTIPLY;
            case '/' -> DIVIDE;
            case '%' -> MOD;
            default -> throw new IllegalArgumentException("Invalid bitwise operator");
        };
    }

    @Override
    public int precedence() {
        return mPrecedence;
    }

    @Override
    public final boolean isRightAssociative() {
        return this == STUPID_POW;
    }
}
