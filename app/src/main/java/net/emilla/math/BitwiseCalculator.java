package net.emilla.math;

import static net.emilla.math.Maths.malformedExpression;
import static net.emilla.math.Maths.undefined;
import static java.lang.Character.isWhitespace;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.math.CalcToken.BitwiseToken;
import net.emilla.math.CalcToken.LParen;
import net.emilla.math.CalcToken.RParen;
import net.emilla.util.Strings;

import java.util.Arrays;
import java.util.Iterator;

public final class BitwiseCalculator {

    private static final class OpStack {

        final BitwiseOperator[] arr;
        int size = 0;

        @StringRes
        final int errorTitle;

        OpStack(int capacity, @StringRes int errorTitle) {
            arr = new BitwiseOperator[capacity];
            this.errorTitle = errorTitle;
        }

        void push(@Nullable BitwiseOperator val) {
            arr[size++] = val;
        }

        @Nullable
        BitwiseOperator peek() {
            if (size < 1) throw malformedExpression(errorTitle);
            return arr[size - 1];
        }

        @Nullable
        BitwiseOperator pop() {
            if (size < 1) throw malformedExpression(errorTitle);
            return arr[--size];
        }

        boolean notEmpty() {
            return size > 0;
        }
    }

    private static final class ValStack {

        final long[] vals;
        final SignStack signs;
        int size = 0;

        @StringRes
        final int errorTitle;

        ValStack(int capacity, @StringRes int errorTitle) {
            vals = new long[capacity];
            signs = new SignStack(capacity, errorTitle);

            this.errorTitle = errorTitle;
        }

        void push(long operand) {
            while (signs.notEmpty()) {
                if (signs.peek() == BitwiseSign.LPAREN) break;
                operand = signs.pop().apply(operand);
                // peek is valid, therefore pop is valid.
            }

            vals[size] = operand;
            ++size;
        }

        void squish(BitwiseOperator op) {
            if (size < 2) throw malformedExpression(errorTitle);

            --size;
            vals[size - 1] = op.apply(vals[size - 1], vals[size]);
        }

        void applyOperator(BitwiseSign op) {
            if (op.postfix) {
                int last = size - 1;
                vals[last] = op.apply(vals[last]);
            } else if (op != BitwiseSign.POSITIVE) signs.push(op);
        }

        void applyOperator(BitwiseOperator op, OpStack opStk) {
            while (opStk.notEmpty()) {
                BitwiseOperator peek = opStk.peek();
                if (peek == BitwiseOperator.LPAREN || op.precedence > peek.precedence
                ||  op.rightAssociative && op.precedence == peek.precedence) break;
                squish(opStk.pop()); // peek is valid, therefore pop is valid.
            }
            opStk.push(op);
        }

        void applyLParen() {
            signs.push(BitwiseSign.LPAREN);
        }

        void applyRParen(OpStack opStk) {
            while (opStk.notEmpty()) {
                BitwiseOperator pop = opStk.pop();
                if (pop == BitwiseOperator.LPAREN) break;

                squish(pop);
            }

            if (signs.notEmpty()) closeSignParen();
        }

        void applyRemainingSigns() {
            while (signs.notEmpty()) closeSignParen();
        }

        private void closeSignParen() {
            if (signs.peek() == BitwiseSign.LPAREN) {
                signs.pop();
                int last = size - 1;
                while (signs.notEmpty()) {
                    BitwiseSign peek = signs.peek();
                    if (peek == BitwiseSign.LPAREN) break;

                    vals[last] = signs.pop().apply(vals[last]);
                    // peek is valid, therefore pop is valid.
                }
            }
        }

        long value() {
            if (size != 1) throw malformedExpression(errorTitle);
            return vals[0];
        }
    }

    private static final class SignStack {

        final BitwiseSign[] arr;
        int size = 0;

        @StringRes
        final int errorTitle;

        SignStack(int capacity, @StringRes int errorTitle) {
            arr = new BitwiseSign[capacity];

            this.errorTitle = errorTitle;
        }

        void push(@Nullable BitwiseSign val) {
            arr[size++] = val;
        }

        @Nullable
        BitwiseSign peek() {
            if (size < 1) throw malformedExpression(errorTitle);
            return arr[size - 1];
        }

        @Nullable
        BitwiseSign pop() {
            if (size < 1) throw malformedExpression(errorTitle);
            return arr[--size];
        }

        boolean notEmpty() {
            return size > 0;
        }
    }

    public static long compute(String expression, @StringRes int errorTitle) {
        int len = expression.length();
        var opStk = new OpStack(len, errorTitle);
        var result = new ValStack(len, errorTitle);

    try {
        for (BitwiseToken token : new BitwiseTokens(expression, errorTitle)) {
            if (token instanceof BitwiseOperator op) {
                result.applyOperator(op, opStk);
            } else if (token instanceof BitwiseSign op) {
                result.applyOperator(op);
            } else if (token instanceof LParen) {
                result.applyLParen();
                opStk.push(BitwiseOperator.LPAREN);
            } else if (token instanceof RParen) {
                result.applyRParen(opStk);
            } else if (token instanceof IntegerNumber num) {
                result.push(num.value);
            }
        }

        while (opStk.notEmpty()) {
            BitwiseOperator pop = opStk.pop();
            if (pop != BitwiseOperator.LPAREN) result.squish(pop);
            else while (opStk.notEmpty()) {
                if (opStk.peek() == BitwiseOperator.LPAREN) opStk.pop();
                else result.applyRParen(opStk);
            }
        }

        result.applyRemainingSigns();
    } catch (ArithmeticException e) {
        throw undefined(errorTitle);
    }

        return result.value();
    }

    private record BitwiseTokens(
        String expression,
        @StringRes int errorTitle
    ) implements Iterable<BitwiseToken> {

        enum Type {
            LPAREN, RPAREN, OPERATOR, NUMBER
        }

        @Override
        public Iterator<BitwiseToken> iterator() {
            return new BitwiseIterator(expression, errorTitle);
        }

        static final class BitwiseIterator implements Iterator<BitwiseToken> {

            final char[] expr;
            final int length;

            int pos;
            Type prevType = Type.LPAREN;
            // an imaginary leading parenthesis gets us the behavior we want without worrying about
            // field nullity.

            @StringRes
            private final int errorTitle;

            BitwiseIterator(String expression, @StringRes int errorTitle) {
                expr = expression.toCharArray();
                length = expr.length;
                pos = Strings.indexOfNonSpace(expr);

                this.errorTitle = errorTitle;
            }

            @Override
            public boolean hasNext() {
                return pos < length;
            }

            @Override
            public BitwiseToken next() {
                return switch (expr[pos]) {
                    case '+', '-' -> switch (prevType) {
                        case LPAREN, OPERATOR -> extractUnary(false);
                        case RPAREN, NUMBER -> extractOperator();
                    };
                    case '~' -> switch (prevType) {
                        case LPAREN, OPERATOR -> extractUnary(false);
                        case RPAREN, NUMBER -> phantomStar();
                        // note that a binary-looking tilde is treated with adjacency multiplication.
                    };
                    case '!' -> switch (prevType) {
                        case LPAREN, OPERATOR -> throw malformedExpression(errorTitle);
                        case RPAREN, NUMBER -> extractUnary(true);
                    };
                    case '|', '^', '&', '*', '/', '%' -> switch (prevType) {
                        case LPAREN, OPERATOR -> throw malformedExpression(errorTitle);
                        case RPAREN, NUMBER -> extractOperator();
                    };
                    case '<', '>' -> switch (prevType) {
                        case LPAREN, OPERATOR -> throw malformedExpression(errorTitle);
                        case RPAREN, NUMBER -> extractShift();
                    };
                    case '(' -> switch (prevType) {
                        case LPAREN, OPERATOR -> extractLParen();
                        case RPAREN, NUMBER -> phantomStar();
                    };
                    case ')' -> switch (prevType) {
                        case LPAREN, OPERATOR -> throw malformedExpression(errorTitle);
                        case RPAREN, NUMBER -> extractRParen();
                    };
                    case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> switch (prevType) {
                        case LPAREN, OPERATOR -> extractNumber();
                        case RPAREN, NUMBER -> phantomStar();
                        // note that this treats space-separated numbers as multiplication.
                    };
                    default -> throw malformedExpression(errorTitle);
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

            private BitwiseSign extractUnary(boolean postfix) {
                var type = postfix ? Type.NUMBER : Type.OPERATOR;
                // postfix unaries are applied immediately, so act like token was a number.
                return BitwiseSign.of(extractChar(type));
            }

            private BitwiseOperator extractOperator() {
                return BitwiseOperator.of(String.valueOf(extractChar(Type.OPERATOR)));
            }

            private char extractChar(Type type) {
                char c = expr[pos];
                advanceImmediate();
                prevType = type;
                return c;
            }

            private BitwiseOperator extractShift() {
                char shiftType = expr[pos];
                ++pos;
                if (!nextCharIs(shiftType)) throw malformedExpression(errorTitle);

                ++pos;
                BitwiseOperator op;
                if (shiftType == '>' && nextCharIs('>')) {
                    op = BitwiseOperator.USHR;
                    advanceImmediate();
                } else {
                    op = BitwiseOperator.of(Strings.repeat(shiftType, 2));
                    advance();
                }

                prevType = Type.OPERATOR;
                return op;
            }

            private boolean nextCharIs(char c) {
                return pos < length && expr[pos] == c;
            }

            private BitwiseOperator phantomStar() {
                prevType = Type.OPERATOR;
                return BitwiseOperator.TIMES;
            }

            private void advanceImmediate() {
                do ++pos;
                while (pos < length && isWhitespace(expr[pos]));
            }

            private void advance() {
                while (pos < length && isWhitespace(expr[pos])) {
                    ++pos;
                }
            }

            private IntegerNumber extractNumber() {
                int start = pos;

                do ++pos;
                while (pos < length && isNumberChar(expr[pos]));

                var s = new String(Arrays.copyOfRange(expr, start, pos));
                advance();

                prevType = Type.NUMBER;
                return new IntegerNumber(s, errorTitle);
            }

            private static boolean isNumberChar(char c) {
                return switch (c) {
                    case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> true;
                    default -> false;
                };
            }
        }
    }

    private BitwiseCalculator() {}
}
