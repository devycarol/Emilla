package net.emilla.math;

/*internal*/ enum UnaryOperator implements InfixToken {
    POSITIVE(false) {
        @Override
        double apply(double n) {
            return n;
        }
    },
    NEGATIVE(false) {
        @Override
        double apply(double n) {
            return -n;
        }
    },
    PERCENT(true) {
        @Override
        double apply(double n) {
            return n / 100.0;
        }
    },
    FACTORIAL(true) {
        @Override
        double apply(double n) {
            return Maths.factorial(n);
        }
    };

    static final UnaryOperator LPAREN = null;

    static UnaryOperator of(char token) {
        return switch (token) {
            case '+' -> POSITIVE;
            case '-' -> NEGATIVE;
            case '%' -> PERCENT;
            case '!' -> FACTORIAL;
            default -> throw new IllegalArgumentException();
        };
    }

    final boolean postfix;

    UnaryOperator(boolean postfix) {
        this.postfix = postfix;
    }

    abstract double apply(double n);
}
