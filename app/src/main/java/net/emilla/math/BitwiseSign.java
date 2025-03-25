package net.emilla.math;

/*internal*/ enum BitwiseSign implements BitwiseToken {
    POSITIVE {
        @Override
        long apply(long n) {
            return n;
        }
    },
    NEGATIVE {
        @Override
        long apply(long n) {
            return -n;
        }
    },
    NOT {
        @Override
        long apply(long n) {
            return ~n;
        }
    };

    static final BitwiseSign LPAREN = null;

    static BitwiseSign of(char token) {
        return switch (token) {
            case '+' -> POSITIVE;
            case '-' -> NEGATIVE;
            case '~' -> NOT;
            default -> throw new IllegalArgumentException();
        };
    }

    final boolean postfix = false;

    abstract long apply(long n);
}
