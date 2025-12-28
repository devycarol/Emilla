package net.emilla.math;

import androidx.annotation.Nullable;

import net.emilla.math.CalcToken.BitwiseToken;

enum BitwiseSign implements BitwiseToken {
    POSITIVE(false) {
        @Override
        public long apply(long n) {
            return n;
        }
    },
    NEGATIVE(false) {
        @Override
        public long apply(long n) {
            return -n;
        }
    },
    NOT(false) {
        @Override
        public long apply(long n) {
            return ~n;
        }
    },
    FACTORIAL(true) {
        @Override
        public long apply(long n) {
            return Maths.factorial(n);
        }
    };

    public abstract long apply(long n);

    public final boolean postfix;

    BitwiseSign(boolean postfix) {
        this.postfix = postfix;
    }

    @Nullable
    public static final BitwiseSign LPAREN = null;

    public static BitwiseSign of(char token) {
        return switch (token) {
            case '+' -> POSITIVE;
            case '-' -> NEGATIVE;
            case '~' -> NOT;
            case '!' -> FACTORIAL;
            default -> throw new IllegalArgumentException("Invalid bitwise sign");
        };
    }

}
