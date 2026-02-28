package net.emilla.math;

enum BitwiseSign implements CalcSign<Long>, BitwiseToken {
    POSITIVE {
        @Override
        public Long apply(Long n) {
            return n;
        }
    },
    NEGATIVE {
        @Override
        public Long apply(Long n) {
            return -n;
        }
    },
    NOT {
        @Override
        public Long apply(Long n) {
            return ~n;
        }
    },
    FACTORIAL {
        @Override
        public Long apply(Long n) {
            return Maths.factorial(n);
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
