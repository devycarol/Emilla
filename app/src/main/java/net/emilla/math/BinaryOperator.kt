package net.emilla.math;

/*internal*/ enum BinaryOperator implements InfixToken {
    ADD(1, false) {
        @Override
        double apply(double a, double b) {
            return a + b;
        }
    },
    SUBTRACT(1, false) {
        @Override
        double apply(double a, double b) {
            return a - b;
        }
    },
    TIMES(2, false) {
        @Override
        double apply(double a, double b) {
            return a * b;
        }
    },
    DIV(2, false) {
        @Override
        double apply(double a, double b) {
            return a / b;
        }
    },
    POW(3, true) {
        @Override
        double apply(double a, double b) {
            return Math.pow(a, b);
        }
    };

    static BinaryOperator of(char token) {
        // todo: nat-language words like "add", "to the power of", ..
        return switch (token) {
            case '+' -> ADD;
            case '-' -> SUBTRACT;
            case '*' -> TIMES;
            case '/' -> DIV;
            case '^' -> POW;
            default -> throw new IllegalArgumentException();
        };
    }

    static final BinaryOperator LPAREN = null;

    final int precedence;
    final boolean rightAssociative;

    BinaryOperator(int precedence, boolean rightAssociative) {
        this.precedence = precedence;
        this.rightAssociative = rightAssociative;
    }

    abstract double apply(double a, double b);
}
