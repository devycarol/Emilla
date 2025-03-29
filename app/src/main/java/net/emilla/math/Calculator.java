package net.emilla.math;

import static net.emilla.math.Maths.malformedExpression;
import static java.lang.Character.isWhitespace;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.math.CalcToken.LParen;
import net.emilla.math.CalcToken.RParen;
import net.emilla.util.Strings;

import java.util.Arrays;
import java.util.Iterator;

public final class Calculator {

//    private static final String ALL_OF = "all ?of", DONE = "done|good";
//
//    // The order in which these are parsed matters because of duplicate words in phrases like "3 by 3" versus "3 divide by 3"
//    private static final String
//            ADDITION = " *(add|plus|and|with|pos(itive)?) *",
//            SUBTRACTION = " *(sub(tract)?|minus|without|neg(ative)?) *",
//            DIVISION = " *(div(ided?( *by)?)?|over) *", // this conflicts with the potential behavior of submitting the commmand when the user utters "over"
//            MULTIPLICATION = " *(mul(t(ipl(y|ied *by))?)?|times|x|of|by) *",
//            EXPONENTIATION = " *(exp(onent)?|(raised? *)?to( *the( *power *of)?)?|pow(er)?|\\*\\*) *", // TTS could confuse 'to' for the number 'two'
//            DECIMAL = " *(point|dot) *",
//            START_PAREN = " *start *",
//            END_PAREN = " *(all|end) *";

    private static final class OpStack {

        final BinaryOperator[] arr;
        int size = 0;

        @StringRes
        final int errorTitle;

        OpStack(int capacity, @StringRes int errorTitle) {
            arr = new BinaryOperator[capacity];

            this.errorTitle = errorTitle;
        }

        void push(@Nullable BinaryOperator val) {
            arr[size++] = val;
        }

        @Nullable
        BinaryOperator peek() {
            if (size < 1) throw malformedExpression(errorTitle);
            return arr[size - 1];
        }

        @Nullable
        BinaryOperator pop() {
            if (size < 1) throw malformedExpression(errorTitle);
            return arr[--size];
        }

        boolean notEmpty() {
            return size > 0;
        }
    }

    private static final class ValStack {

        final double[] vals;
        final SignStack signs;
        int size = 0;

        @StringRes
        final int errorTitle;

        ValStack(int capacity, @StringRes int errorTitle) {
            vals = new double[capacity];
            signs = new SignStack(capacity, errorTitle);

            this.errorTitle = errorTitle;
        }

        void push(double operand) {
            while (signs.notEmpty()) {
                if (signs.peek() == UnaryOperator.LPAREN) break;
                operand = signs.pop().apply(operand);
                // peek is valid, therefore pop is valid.
            }

            vals[size] = operand;
            ++size;
        }

        void squish(BinaryOperator op) {
            if (size < 2) throw malformedExpression(errorTitle);

            --size;
            vals[size - 1] = op.apply(vals[size - 1], vals[size]);
        }

        void applyOperator(UnaryOperator op) {
            if (op.postfix) {
                int last = size - 1;
                vals[last] = op.apply(vals[last]);
            } else if (op != UnaryOperator.POSITIVE) signs.push(op);
        }

        void applyOperator(BinaryOperator op, OpStack opStk) {
            while (opStk.notEmpty()) {
                BinaryOperator peek = opStk.peek();
                if (peek == BinaryOperator.LPAREN || op.precedence > peek.precedence
                ||  op.rightAssociative && op.precedence == peek.precedence) break;
                squish(opStk.pop()); // peek is valid, therefore pop is valid.
            }
            opStk.push(op);
        }

        void applyLParen() {
            signs.push(UnaryOperator.LPAREN);
        }

        void applyRParen(OpStack opStk) {
            while (opStk.notEmpty()) {
                BinaryOperator pop = opStk.pop();
                if (pop == BinaryOperator.LPAREN) break;

                squish(pop);
            }

            if (signs.notEmpty()) closeSignParen();
        }

        void applyRemainingSigns() {
            while (signs.notEmpty()) closeSignParen();
        }

        private void closeSignParen() {
            if (signs.peek() == UnaryOperator.LPAREN) {
                signs.pop();
                int last = size - 1;
                while (signs.notEmpty()) {
                    UnaryOperator peek = signs.peek();
                    if (peek == UnaryOperator.LPAREN) break;

                    vals[last] = signs.pop().apply(vals[last]);
                    // peek is valid, therefore pop is valid.
                }
            }
        }

        double value() {
            if (size != 1) throw malformedExpression(errorTitle);
            return vals[0];
        }
    }

    private static final class SignStack {

        final UnaryOperator[] arr;
        int size = 0;

        @StringRes
        final int errorTitle;

        SignStack(int capacity, @StringRes int errorTitle) {
            arr = new UnaryOperator[capacity];

            this.errorTitle = errorTitle;
        }

        void push(@Nullable UnaryOperator sign) {
            arr[size++] = sign;
        }

        @Nullable
        UnaryOperator peek() {
            if (size < 1) throw malformedExpression(errorTitle);
            return arr[size - 1];
        }

        @Nullable
        UnaryOperator pop() {
            if (size < 1) throw malformedExpression(errorTitle);
            return arr[--size];
        }

        boolean notEmpty() {
            return size > 0;
        }
    }

    public static double compute(String expression, @StringRes int errorTitle) {
        int len = expression.length();
        var opStk = new OpStack(len, errorTitle);
        var result = new ValStack(len, errorTitle);

        for (InfixToken token : new InfixTokens(expression, errorTitle)) {
            if (token instanceof BinaryOperator op) {
                result.applyOperator(op, opStk);
            } else if (token instanceof UnaryOperator op) {
                result.applyOperator(op);
            } else if (token instanceof LParen) {
                result.applyLParen();
                opStk.push(BinaryOperator.LPAREN);
            } else if (token instanceof RParen) {
                result.applyRParen(opStk);
            } else if (token instanceof FloatingPointNumber num) {
                result.push(num.val);
            }
        }

        while (opStk.notEmpty()) {
            BinaryOperator pop = opStk.pop();
            if (pop != BinaryOperator.LPAREN) result.squish(pop);
            else while (opStk.notEmpty()) {
                if (opStk.peek() == BinaryOperator.LPAREN) opStk.pop();
                else result.applyRParen(opStk);
            }
        }

        result.applyRemainingSigns();

        return result.value();
    }

    private record InfixTokens(
        String expression,
        @StringRes int errorTitle
    ) implements Iterable<InfixToken> {

        enum Type {
            LPAREN, RPAREN, OPERATOR, NUMBER
        }

        @Override
        public Iterator<InfixToken> iterator() {
            return new InfixIterator(expression, errorTitle);
        }

        static final class InfixIterator implements Iterator<InfixToken> {

            final char[] expr;
            final int length;

            int pos;
            Type prevType = Type.LPAREN;
            // an imaginary leading parenthesis gets us the behavior we want without worrying about
            // field nullity.

            @StringRes
            private final int errorTitle;

            InfixIterator(String expression, @StringRes int errorTitle) {
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
            public InfixToken next() {
                return switch (expr[pos]) {
                    case '+', '-' -> switch (prevType) {
                        case LPAREN, OPERATOR -> extractUnary(false);
                        case RPAREN, NUMBER -> extractOperator();
                    };
                    case '%' -> switch (prevType) {
                        case LPAREN, OPERATOR -> throw malformedExpression(errorTitle);
                        case RPAREN, NUMBER -> extractUnary(true);
                    };
                    case '*', '/', '^' -> switch (prevType) {
                        case LPAREN, OPERATOR -> throw malformedExpression(errorTitle);
                        case RPAREN, NUMBER -> extractOperator();
                    };
                    case '(' -> switch (prevType) {
                        case LPAREN, OPERATOR -> extractLParen();
                        case RPAREN, NUMBER -> phantomStar();
                    };
                    case ')' -> switch (prevType) {
                        case LPAREN, OPERATOR -> throw malformedExpression(errorTitle);
                        case RPAREN, NUMBER -> extractRParen();
                    };
                    case '.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> switch (prevType) {
                        case LPAREN, OPERATOR -> extractNumber(pos);
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

            private UnaryOperator extractUnary(boolean postfix) {
                var type = postfix ? Type.NUMBER : Type.OPERATOR;
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
                do ++pos;
                while (pos < length && isWhitespace(expr[pos]));
            }

            private void advance() {
                while (pos < length && isWhitespace(expr[pos])) {
                    ++pos;
                }
            }

            private FloatingPointNumber extractNumber(int start) {
                do ++pos;
                while (pos < length && isNumberChar(expr[pos]));

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

    private Calculator() {}
}
