package net.emilla.math;

/*internal*/ enum BitwiseOperator implements BitwiseToken {
    OR(-3) {
        @Override
        long apply(long a, long b) {
            return a | b;
        }
    },
    XOR(-2) {
        @Override
        long apply(long a, long b) {
            return a ^ b;
        }
    },
    AND(-1) {
        @Override
        long apply(long a, long b) {
            return a & b;
        }
    },
    LSHIFT(0) {
        @Override
        long apply(long a, long b) {
            return a << b;
        }
    },
    RSHIFT(0) {
        @Override
        long apply(long a, long b) {
            return a >> b;
        }
    },
    URSHIFT(0) {
        @Override
        long apply(long a, long b) {
            return a >>> b;
        }
    },
    ADD(1) {
        @Override
        long apply(long a, long b) {
            return a + b;
        }
    },
    SUBTRACT(1) {
        @Override
        long apply(long a, long b) {
            return a - b;
        }
    },
    TIMES(2) {
        @Override
        long apply(long a, long b) {
            return a * b;
        }
    },
    DIV(2) {
        @Override
        long apply(long a, long b) {
            return a / b;
        }
    },
    MOD(2) {
        @Override
        long apply(long a, long b) {
            return a % b;
        }
    };

    static BitwiseOperator of(String token) {
        // todo: nat-language words like "add", "to the power of", ..
        return switch (token) {
            case "|" -> OR;
            case "^" -> XOR;
            case "&" -> AND;
            case "<<" -> LSHIFT;
            case ">>" -> RSHIFT;
            case ">>>" -> URSHIFT;
            case "+" -> ADD;
            case "-" -> SUBTRACT;
            case "*" -> TIMES;
            case "/" -> DIV;
            case "%" -> MOD;
            default -> throw new IllegalArgumentException();
        };
    }

    static final BitwiseOperator LPAREN = null;

    final int precedence;
    final boolean rightAssociative = false;

    BitwiseOperator(int precedence) {
        this.precedence = precedence;
    }

    abstract long apply(long a, long b);
}
