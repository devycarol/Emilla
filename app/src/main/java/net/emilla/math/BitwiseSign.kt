package net.emilla.math;

/*internal*/ enum BitwiseSign implements BitwiseToken {
    POSITIVE(false) {
        @Override
        long apply(long n) {
            return n;
        }
    },
    NEGATIVE(false) {
        @Override
        long apply(long n) {
            return -n;
        }
    },
    NOT(false) {
        @Override
        long apply(long n) {
            return ~n;
        }
    },
    FACTORIAL(true) {
        @Override
        long apply(long n) {
            return Maths.factorial(n);
        }
    };

    static final BitwiseSign LPAREN = null;

    static BitwiseSign of(char token) {
        return switch (token) {
            case '+' -> POSITIVE;
            case '-' -> NEGATIVE;
            case '~' -> NOT;
            case '!' -> FACTORIAL;
            default -> throw new IllegalArgumentException();
        };
    }

    final boolean postfix;

    BitwiseSign(boolean postfix) {
        this.postfix = postfix;
    }

    abstract long apply(long n);
}
