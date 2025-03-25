package net.emilla.math;

import static net.emilla.math.Maths.malformedExpression;
import static java.lang.Character.isWhitespace;
import static java.lang.Double.parseDouble;

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

        private static BinaryOperator of(char token) {
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

        private static final BinaryOperator LPAREN = null;

        private final int precedence;
        private final boolean rightAssociative;

        BinaryOperator(int precedence, boolean rightAssociative) {
            this.precedence = precedence;
            this.rightAssociative = rightAssociative;
        }

        abstract double apply(double a, double b);
    }

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
        int size = 0;

        @StringRes
        final int errorTitle;

        ValStack(int capacity, @StringRes int errorTitle) {
            vals = new double[capacity];
            this.errorTitle = errorTitle;
        }

        void push(double operand) {
            vals[size] = operand;
            ++size;
        }

        void squish(BinaryOperator op) {
            if (size < 2) throw malformedExpression(errorTitle);

            --size;
            vals[size - 1] = op.apply(vals[size - 1], vals[size]);
        }

        void applyOperator(BinaryOperator op, OpStack opStk) {
            while (opStk.notEmpty()) {
                BinaryOperator peek = opStk.peek();
                if (peek == BinaryOperator.LPAREN || op.precedence > peek.precedence
                ||  op.rightAssociative && op.precedence == peek.precedence) break;
                else squish(opStk.pop()); // peek is valid, therefore pop is valid.
            }
            opStk.push(op);
        }

        void applyRParen(OpStack opStk) {
            while (opStk.notEmpty()) {
                BinaryOperator pop = opStk.pop();
                if (pop == BinaryOperator.LPAREN) break;
                else squish(pop);
            }
        }

        double value() {
            if (size != 1) throw malformedExpression(errorTitle);
            return vals[0];
        }
    }

    public static double compute(String expression, @StringRes int errorTitle) {
        int len = expression.length();
        var opStk = new OpStack(len, errorTitle);
        var result = new ValStack(len, errorTitle);

        for (InfixToken token : new InfixTokens(expression, errorTitle)) {
            if (token instanceof BinaryOperator op) {
                result.applyOperator(op, opStk);
            } else if (token instanceof LParen) {
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

        static class InfixIterator implements Iterator<InfixToken> {

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
                    case '+', '*', '/', '^' -> switch (prevType) {
                        case LPAREN, OPERATOR -> throw malformedExpression(errorTitle);
                        // todo: unary plus ugh.
                        case RPAREN, NUMBER -> extractOperator();
                    };
                    case '(' -> switch (prevType) {
                        case RPAREN, NUMBER -> phantomStar();
                        case LPAREN, OPERATOR -> extractLParen();
                    };
                    case ')' -> switch (prevType) {
                        case LPAREN, OPERATOR -> throw malformedExpression(errorTitle);
                        case RPAREN, NUMBER -> extractRParen();
                    };
                    case '-' -> switch (prevType) {
                        case LPAREN -> phantomZero();
                        case OPERATOR -> {
                            int start = pos;
                            boolean negative = true;
                            tryAdvanceImmediate();
                            do if (expr[pos] == '-') {
                                if (negative) {
                                    start = tryAdvanceImmediate();
                                    negative = false;
                                } else {
                                    tryAdvanceImmediate();
                                    negative = true;
                                }
                            } while (pos < length && expr[pos] == '-');

                            yield extractNumber(start);
                        }
                        case RPAREN, NUMBER -> extractOperator();
                    };
                    case '.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> switch (prevType) {
                        case RPAREN, NUMBER -> phantomStar();
                        // note that this treats space-separated numbers as multiplication.
                        case LPAREN, OPERATOR -> extractNumber(pos);
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

            private BinaryOperator extractOperator() {
                return BinaryOperator.of(extractChar(Type.OPERATOR));
            }

            private char extractChar(Type type) {
                char c = expr[pos];
                advanceImmediate();
                prevType = type;
                return c;
            }

            private FloatingPointNumber phantomZero() {
                prevType = Type.NUMBER;
                return new FloatingPointNumber(0.0);
            }

            private BinaryOperator phantomStar() {
                prevType = Type.OPERATOR;
                return BinaryOperator.TIMES;
            }

            private int tryAdvanceImmediate() {
                do if (++pos == length) throw malformedExpression(errorTitle);
                while (isWhitespace(expr[pos]));
                return pos;
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

                if (nextCharIs('%')) {
                    // postfix unary 'percent' sign turns the number into a decimal value.
                    double percent = tryParseDouble(s, errorTitle) / 100.0;

                    advanceImmediate();
                    s = String.valueOf(percent);
                }

                prevType = Type.NUMBER;
                return new FloatingPointNumber(tryParseDouble(s, errorTitle));
            }

            private boolean nextCharIs(char c) {
                return pos < length && expr[pos] == c;
            }

            private static boolean isNumberChar(char c) {
                return switch (c) {
                    case '.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> true;
                    // note that unary minus isn't included for this method.
                    // todo: scientific notation?
                    default -> false;
                };
            }
        }
    }

    private static double tryParseDouble(String operand, @StringRes int errorTitle) { try {
        return parseDouble(operand);
    } catch (NumberFormatException e) {
        throw malformedExpression(errorTitle);
    }}

    private Calculator() {}
}
