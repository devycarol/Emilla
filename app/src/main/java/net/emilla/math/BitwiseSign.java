package net.emilla.math;

import java.math.BigInteger;

enum BitwiseSign implements CalcSign<BigInteger>, BitwiseToken {
    POSITIVE {
        @Override
        public BigInteger apply(BigInteger n) {
            return n;
        }
    },
    NEGATIVE {
        @Override
        public BigInteger apply(BigInteger n) {
            return n.negate();
        }
    },
    NOT {
        @Override
        public BigInteger apply(BigInteger n) {
            return n.not();
        }
    },
    FACTORIAL {
        @Override
        public BigInteger apply(BigInteger n) {
            return Maths.stupidFactorial(n);
        }
    }
;
    private static final BitwiseSign[] sValues = values();

    public static BitwiseSign of(int ordinal) {
        return sValues[ordinal];
    }

    public static BitwiseSign of(char token) {
        return switch (token) {
            case '+' -> POSITIVE;
            case '-' -> NEGATIVE;
            case '~' -> NOT;
            case '!' -> FACTORIAL;
            default -> throw new IllegalArgumentException("Invalid bitwise sign");
        };
    }

    @Override
    public final boolean isPostfix() {
        return this == FACTORIAL;
    }

    @Override
    public final boolean isIdempotent() {
        return this == POSITIVE;
    }
}
