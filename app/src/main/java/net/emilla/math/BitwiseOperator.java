package net.emilla.math;

enum BitwiseOperator implements CalcOperator<Long>, BitwiseToken {
    OR(-3) {
        @Override
        public Long apply(Long a, Long b) {
            return a | b;
        }
    },
    XOR(-2) {
        @Override
        public Long apply(Long a, Long b) {
            return a ^ b;
        }
    },
    AND(-1) {
        @Override
        public Long apply(Long a, Long b) {
            return a & b;
        }
    },
    SHL(0) {
        @Override
        public Long apply(Long a, Long b) {
            return a << b;
        }
    },
    SHR(0) {
        @Override
        public Long apply(Long a, Long b) {
            return a >> b;
        }
    },
    USHR(0) {
        @Override
        public Long apply(Long a, Long b) {
            return a >>> b;
        }
    },
    PLUS(1) {
        @Override
        public Long apply(Long a, Long b) {
            return a + b;
        }
    },
    MINUS(1) {
        @Override
        public Long apply(Long a, Long b) {
            return a - b;
        }
    },
    TIMES(2) {
        @Override
        public Long apply(Long a, Long b) {
            return a * b;
        }
    },
    DIV(2) {
        @Override
        public Long apply(Long a, Long b) {
            return a / b;
        }
    },
    MOD(2) {
        @Override
        public Long apply(Long a, Long b) {
            return a % b;
        }
    },
    POW(3) {
        @Override
        public Long apply(Long a, Long b) {
            return (long) Math.pow((double) a, (double) b);
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

    public static BitwiseOperator of(String token) {
        return switch (token) {
            case "|" -> OR;
            case "^" -> XOR;
            case "&" -> AND;
            case "<<" -> SHL;
            case ">>" -> SHR;
            case ">>>" -> USHR;
            case "+" -> PLUS;
            case "-" -> MINUS;
            case "*" -> TIMES;
            case "/" -> DIV;
            case "%" -> MOD;
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
