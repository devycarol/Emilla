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
    SHL(0) {
        @Override
        public BigInteger apply(BigInteger a, BigInteger b) {
            return a.shiftLeft(Maths.exactInt(b));
        }
    },
    SHR(0) {
        @Override
        public BigInteger apply(BigInteger a, BigInteger b) {
            return a.shiftRight(Maths.exactInt(b));
        }
    },
    PLUS(1) {
        @Override
        public BigInteger apply(BigInteger a, BigInteger b) {
            return a.add(b);
        }
    },
    MINUS(1) {
        @Override
        public BigInteger apply(BigInteger a, BigInteger b) {
            return a.subtract(b);
        }
    },
    TIMES(2) {
        @Override
        public BigInteger apply(BigInteger a, BigInteger b) {
            return a.multiply(b);
        }
    },
    DIV(2) {
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
    POW(3) {
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
            case '<' -> SHL;
            case '>' -> SHR;
            case '+' -> PLUS;
            case '-' -> MINUS;
            case '*' -> TIMES;
            case '/' -> DIV;
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
        return this == POW;
    }
}
