package net.emilla.math;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.math.CalcToken.InfixToken;
import net.emilla.math.CalcToken.LParen;
import net.emilla.math.CalcToken.RParen;
import net.emilla.util.Strings;

import java.util.Arrays;
import java.util.Iterator;

public enum Calculator {;
    private static final class OperatorStack {
        final BinaryOperator[] array;
        @StringRes
        final int errorTitle;
        int size = 0;

        OperatorStack(int capacity, @StringRes int errorTitle) {
            this.array = new BinaryOperator[capacity];
            this.errorTitle = errorTitle;
        }

        void push(@Nullable BinaryOperator val) {
            array[size] = val;
            ++size;
        }

        @Nullable
        BinaryOperator peek() {
            if (size < 1) {
                throw Maths.malformedExpression(errorTitle);
            }

            return array[size - 1];
        }

        @Nullable
        BinaryOperator pop() {
            if (size < 1) {
                throw Maths.malformedExpression(errorTitle);
            }

            --size;
            return array[size];
        }

        boolean notEmpty() {
            return size > 0;
        }
    }

    private static final class ValStack {
        final double[] vals;
        final SignStack signs;
        @StringRes
        final int errorTitle;
        int size = 0;

        ValStack(int capacity, @StringRes int errorTitle) {
            this.vals = new double[capacity];
            this.signs = new SignStack(capacity, errorTitle);
            this.errorTitle = errorTitle;
        }

        void push(double operand) {
            while (signs.notEmpty()) {
                if (signs.peek() == UnaryOperator.LPAREN) {
                    break;
                }

                operand = signs.pop().apply(operand);
                // peek is valid, therefore pop is valid.
            }

            vals[size] = operand;
            ++size;
        }

        void squish(BinaryOperator op) {
            if (size < 2) {
                throw Maths.malformedExpression(errorTitle);
            }

            --size;
            vals[size - 1] = op.apply(vals[size - 1], vals[size]);
        }

        void applyOperator(UnaryOperator op) {
            if (op.postfix) {
                int last = size - 1;
                vals[last] = op.apply(vals[last]);
            } else if (op != UnaryOperator.POSITIVE) {
                signs.push(op);
            }
        }

        void applyOperator(BinaryOperator op, OperatorStack operators) {
            while (operators.notEmpty()) {
                BinaryOperator peek = operators.peek();
                if (peek == BinaryOperator.LPAREN
                    || op.precedence > peek.precedence
                    || op.rightAssociative && op.precedence == peek.precedence) {
                    break;
                }
                squish(operators.pop()); // peek is valid, therefore pop is valid.
            }
            operators.push(op);
        }

        void applyLParen() {
            signs.push(UnaryOperator.LPAREN);
        }

        void applyRParen(OperatorStack operators) {
            while (operators.notEmpty()) {
                BinaryOperator pop = operators.pop();
                if (pop == BinaryOperator.LPAREN) {
                    break;
                }

                squish(pop);
            }

            if (signs.notEmpty()) {
                closeSignParen();
            }
        }

        void applyRemainingSigns() {
            while (signs.notEmpty()) {
                closeSignParen();
            }
        }

        private void closeSignParen() {
            if (signs.peek() == UnaryOperator.LPAREN) {
                signs.pop();
                int last = size - 1;
                while (signs.notEmpty()) {
                    UnaryOperator peek = signs.peek();
                    if (peek == UnaryOperator.LPAREN) {
                        break;
                    }

                    vals[last] = signs.pop().apply(vals[last]);
                    // peek is valid, therefore pop is valid.
                }
            }
        }

        double value() {
            if (size != 1) {
                throw Maths.malformedExpression(errorTitle);
            }

            return vals[0];
        }
    }

    private static final class SignStack {
        final UnaryOperator[] arr;
        @StringRes
        final int errorTitle;
        int size = 0;

        SignStack(int capacity, @StringRes int errorTitle) {
            this.arr = new UnaryOperator[capacity];
            this.errorTitle = errorTitle;
        }

        void push(@Nullable UnaryOperator sign) {
            arr[size] = sign;
            ++size;
        }

        @Nullable
        UnaryOperator peek() {
            if (size < 1) {
                throw Maths.malformedExpression(errorTitle);
            }

            return arr[size - 1];
        }

        @Nullable
        UnaryOperator pop() {
            if (size < 1) {
                throw Maths.malformedExpression(errorTitle);
            }

            --size;
            return arr[size];
        }

        boolean notEmpty() {
            return size > 0;
        }
    }

    public static double compute(String expression, @StringRes int errorTitle) {
        // Todo: use big decimals to reduce overflow, esp. in operations like factorial.

        int len = expression.length();
        var operators = new OperatorStack(len, errorTitle);
        var result = new ValStack(len, errorTitle);

    try {
        for (InfixToken token : new InfixTokens(expression, errorTitle)) {
            switch (token) {
            case BinaryOperator op -> result.applyOperator(op, operators);
            case UnaryOperator op -> result.applyOperator(op);
            case LParen __ -> {
                result.applyLParen();
                operators.push(BinaryOperator.LPAREN);
            }
            case RParen __ -> result.applyRParen(operators);
            case FloatingPointNumber number -> result.push(number.value);
            }
        }

        while (operators.notEmpty()) {
            BinaryOperator pop = operators.pop();
            if (pop != BinaryOperator.LPAREN) {
                result.squish(pop);
            } else {
                while (operators.notEmpty()) {
                    if (operators.peek() == BinaryOperator.LPAREN) {
                        operators.pop();
                    } else {
                        result.applyRParen(operators);
                    }
                }
            }
        }

        result.applyRemainingSigns();
    } catch (ArithmeticException e) {
        throw Maths.undefined(errorTitle);
    }

        return result.value();
    }

    private record InfixTokens(String expression, @StringRes int errorTitle)
        implements Iterable<InfixToken>
    {
        enum Type {
            LPAREN,
            RPAREN,
            OPERATOR,
            NUMBER,
        }

        @Override
        public Iterator<InfixToken> iterator() {
            return new InfixIterator(expression, errorTitle);
        }

        static final class InfixIterator implements Iterator<InfixToken> {
            final char[] expr;
            final int length;
            @StringRes
            final int errorTitle;
            int pos;
            Type prevType = Type.LPAREN;
            // an imaginary leading parenthesis gets us the behavior we want without worrying about
            // field nullity.


            InfixIterator(String expression, @StringRes int errorTitle) {
                this.expr = expression.toCharArray();
                this.length = expr.length;
                this.errorTitle = errorTitle;
                this.pos = Strings.indexOfNonSpace(expr);
            }

            @Override
            public boolean hasNext() {
                return pos < length;
            }

            @Override
            public InfixToken next() {
                return switch (expr[pos]) {
                    case '+', '-' -> switch (prevType) {
                        case LPAREN, OPERATOR -> extractUnary(false);
                        case RPAREN, NUMBER -> extractOperator();
                    };
                    case '%', '!' -> switch (prevType) {
                        case LPAREN, OPERATOR -> throw Maths.malformedExpression(errorTitle);
                        case RPAREN, NUMBER -> extractUnary(true);
                    };
                    case '*', '/', '^' -> switch (prevType) {
                        case LPAREN, OPERATOR -> throw Maths.malformedExpression(errorTitle);
                        case RPAREN, NUMBER -> extractOperator();
                    };
                    case '(' -> switch (prevType) {
                        case LPAREN, OPERATOR -> extractLParen();
                        case RPAREN, NUMBER -> phantomStar();
                    };
                    case ')' -> switch (prevType) {
                        case LPAREN, OPERATOR -> throw Maths.malformedExpression(errorTitle);
                        case RPAREN, NUMBER -> extractRParen();
                    };
                    case '.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> switch (prevType) {
                        case LPAREN, OPERATOR -> extractNumber();
                        case RPAREN, NUMBER -> phantomStar();
                        // note that this treats space-separated numbers as multiplication.
                    };
                    default -> throw Maths.malformedExpression(errorTitle);
                };
            }

            private LParen extractLParen() {
                extractChar(Type.LPAREN);
                return LParen.INSTANCE;
            }

            private RParen extractRParen() {
                extractChar(Type.RPAREN);
                return RParen.INSTANCE;
            }

            private UnaryOperator extractUnary(boolean postfix) {
                var type = postfix
                    ? Type.NUMBER
                    : Type.OPERATOR
                ;
                // postfix unaries are applied immediately, so act like token was a number.
                return UnaryOperator.of(extractChar(type));
            }

            private BinaryOperator extractOperator() {
                return BinaryOperator.of(extractChar(Type.OPERATOR));
            }

            private char extractChar(Type type) {
                char c = expr[pos];
                advanceImmediate();
                prevType = type;
                return c;
            }

            private BinaryOperator phantomStar() {
                prevType = Type.OPERATOR;
                return BinaryOperator.TIMES;
            }

            private void advanceImmediate() {
                do {
                    ++pos;
                } while (pos < length && Character.isWhitespace(expr[pos]));
            }

            private void advance() {
                while (pos < length && Character.isWhitespace(expr[pos])) {
                    ++pos;
                }
            }

            private FloatingPointNumber extractNumber() {
                int start = pos;

                do {
                    ++pos;
                } while (pos < length && isNumberChar(expr[pos]));

                var s = new String(Arrays.copyOfRange(expr, start, pos));
                advance();

                prevType = Type.NUMBER;
                return new FloatingPointNumber(s, errorTitle);
            }

            private static boolean isNumberChar(char c) {
                return switch (c) {
                    case '.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> true;
                    // todo: scientific notation?
                    default -> false;
                };
            }
        }
    }
}
