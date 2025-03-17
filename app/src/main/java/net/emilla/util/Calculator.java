package net.emilla.util;

import static java.lang.Character.isWhitespace;
import static java.lang.Double.parseDouble;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.exception.EmillaException;

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

    private enum Operator {
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

        @Nullable
        public static Operator of(String token) {
            // todo: nat-language words like "add", "to the power of", ..
            return switch (token) {
                case "+" -> ADD;
                case "-" -> SUBTRACT;
                case "*" -> TIMES;
                case "/" -> DIV;
                case "^" -> POW;
                default -> null;
            };
        }

        static final Operator LPAREN = null;

        final int precedence;
        final boolean rightAssociative;

        Operator(int precedence, boolean rightAssociative) {
            this.precedence = precedence;
            this.rightAssociative = rightAssociative;
        }

        abstract double apply(double a, double b);
    }

    private static final class OpStack {

        final Operator[] arr;
        int size = 0;

        @StringRes
        final int errorTitle;

        OpStack(int capacity, @StringRes int errorTitle) {
            arr = new Operator[capacity];
            this.errorTitle = errorTitle;
        }

        void push(@Nullable Operator val) {
            arr[size++] = val;
        }

        @Nullable
        Operator peek() {
            if (size < 1) throw malformedExpression(errorTitle);
            return arr[size - 1];
        }

        @Nullable
        Operator pop() {
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

        void push(String operand) { try {
            vals[size] = parseDouble(operand);
            ++size;
        } catch (NumberFormatException e) {
            throw malformedExpression(errorTitle);
        }}

        void squish(Operator op) {
            if (size < 2) throw malformedExpression(errorTitle);

            --size;
            vals[size - 1] = op.apply(vals[size - 1], vals[size]);
        }

        void applyOperator(Operator op, OpStack opStk) {
            while (opStk.notEmpty()) {
                Operator peek = opStk.peek();
                if (peek == Operator.LPAREN || op.precedence > peek.precedence
                ||  op.rightAssociative && op.precedence == peek.precedence) break;
                else squish(opStk.pop()); // peek is valid, therefore pop is valid.
            }
            opStk.push(op);
        }

        void applyRParen(OpStack opStk) {
            while (opStk.notEmpty()) {
                Operator pop = opStk.pop();
                if (pop == Operator.LPAREN) break;
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

        for (String token : new InfixTokens(expression, errorTitle)) {
            var op = Operator.of(token);
            if (op != null) result.applyOperator(op, opStk);
            else switch (token) {
                case "(" -> opStk.push(Operator.LPAREN);
                case ")" -> result.applyRParen(opStk);
                default -> result.push(token);
            }
        }

        while (opStk.notEmpty()) {
            Operator pop = opStk.pop();
            if (pop != Operator.LPAREN) result.squish(pop);
            else while (opStk.notEmpty()) {
                if (opStk.peek() == Operator.LPAREN) opStk.pop();
                else result.applyRParen(opStk);
            }
        }

        return result.value();
    }

    private record InfixTokens(
        String expression,
        @StringRes int errorTitle
    ) implements Iterable<String> {

        enum Type {
            LPAREN, RPAREN, OPERATOR, NUMBER
        }

        @Override
        public Iterator<String> iterator() {
            return new InfixIterator(expression, errorTitle);
        }

        static class InfixIterator implements Iterator<String> {

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
            public String next() {
                return switch (expr[pos]) {
                    case '+', '*', '/', '^' -> switch (prevType) {
                        case LPAREN, OPERATOR -> throw malformedExpression(errorTitle);
                        // todo: unary plus ugh.
                        case RPAREN, NUMBER -> extractChar(Type.OPERATOR);
                    };
                    case '(' -> switch (prevType) {
                        case RPAREN, NUMBER -> phantomStar();
                        case LPAREN, OPERATOR -> extractChar(Type.LPAREN);
                    };
                    case ')' -> switch (prevType) {
                        case LPAREN, OPERATOR -> throw malformedExpression(errorTitle);
                        case RPAREN, NUMBER -> extractChar(Type.RPAREN);
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
                        case RPAREN, NUMBER -> extractChar(Type.OPERATOR);
                    };
                    case '.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> switch (prevType) {
                        case RPAREN, NUMBER -> phantomStar();
                        // note that this treats space-separated numbers as multiplication.
                        case LPAREN, OPERATOR -> extractNumber(pos);
                    };
                    default -> throw malformedExpression(errorTitle);
                };
            }

            private String extractChar(Type type) {
                var s = String.valueOf(expr[pos]);
                advanceImmediate();
                prevType = type;
                return s;
            }

            private String phantomZero() {
                prevType = Type.NUMBER;
                return "0";
            }

            private String phantomStar() {
                prevType = Type.OPERATOR;
                return "*";
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

            private String extractNumber(int start) {
                do ++pos;
                while (pos < length && isNumberChar(expr[pos]));

                var s = new String(Arrays.copyOfRange(expr, start, pos));
                advance();

                prevType = Type.NUMBER;
                return s;
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

    private static EmillaException malformedExpression(@StringRes int errorTitle) {
        return new EmillaException(errorTitle, R.string.error_calc_malformed_expression);
    }

    private Calculator() {}
}
