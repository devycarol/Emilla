package net.emilla.math;

import androidx.annotation.Nullable;

import net.emilla.math.CalcToken.BitwiseToken;

enum BitwiseOperator implements BitwiseToken {
    OR(-3, false) {
        @Override
        public long apply(long a, long b) {
            return a | b;
        }
    },
    XOR(-2, false) {
        @Override
        public long apply(long a, long b) {
            return a ^ b;
        }
    },
    AND(-1, false) {
        @Override
        public long apply(long a, long b) {
            return a & b;
        }
    },
    SHL(0, false) {
        @Override
        public long apply(long a, long b) {
            return a << b;
        }
    },
    SHR(0, false) {
        @Override
        public long apply(long a, long b) {
            return a >> b;
        }
    },
    USHR(0, false) {
        @Override
        public long apply(long a, long b) {
            return a >>> b;
        }
    },
    PLUS(1, false) {
        @Override
        public long apply(long a, long b) {
            return a + b;
        }
    },
    MINUS(1, false) {
        @Override
        public long apply(long a, long b) {
            return a - b;
        }
    },
    TIMES(2, false) {
        @Override
        public long apply(long a, long b) {
            return a * b;
        }
    },
    DIV(2, false) {
        @Override
        public long apply(long a, long b) {
            return a / b;
        }
    },
    MOD(2, false) {
        @Override
        public long apply(long a, long b) {
            return a % b;
        }
    },
    POW(3, true) {
        @Override
        public long apply(long a, long b) {
            return (long) Math.pow((double) a, (double) b);
        }
    };

    public abstract long apply(long a, long b);

    @Nullable
    public static final BitwiseOperator LPAREN = null;

    public final int precedence;
    public final boolean rightAssociative;

    BitwiseOperator(int precedence, boolean rightAssociative) {
        this.precedence = precedence;
        this.rightAssociative = rightAssociative;
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

}
