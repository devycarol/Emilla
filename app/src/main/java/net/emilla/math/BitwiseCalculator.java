package net.emilla.math;

import static java.lang.Character.isWhitespace;
import static java.lang.Long.parseLong;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.exception.EmillaException;
import net.emilla.util.Strings;

import java.util.Arrays;
import java.util.Iterator;

public final class BitwiseCalculator {

    private enum Operator {
        OR(-3) {
            @Override
            long apply(long a, long b) {
                return a | b;
            }
        },
        XOR(-2) {
            @Override
            long apply(long a, long b) {
                return a ^ b;
            }
        },
        AND(-1) {
            @Override
            long apply(long a, long b) {
                return a & b;
            }
        },
        LSHIFT(0) {
            @Override
            long apply(long a, long b) {
                return a << b;
            }
        },
        RSHIFT(0) {
            @Override
            long apply(long a, long b) {
                return a >> b;
            }
        },
        URSHIFT(0) {
            @Override
            long apply(long a, long b) {
                return a >>> b;
            }
        },
        ADD(1) {
            @Override
            long apply(long a, long b) {
                return a + b;
            }
        },
        SUBTRACT(1) {
            @Override
            long apply(long a, long b) {
                return a - b;
            }
        },
        TIMES(2) {
            @Override
            long apply(long a, long b) {
                return a * b;
            }
        },
        DIV(2) {
            @Override
            long apply(long a, long b) {
                return a / b;
            }
        },
        MOD(2) {
            @Override
            long apply(long a, long b) {
                return a % b;
            }
        };

        @Nullable
        public static Operator of(String token) {
            // todo: nat-language words like "add", "to the power of", ..
            return switch (token) {
                case "|" -> OR;
                case "^" -> XOR;
                case "&" -> AND;
                case "<<" -> LSHIFT;
                case ">>" -> RSHIFT;
                case ">>>" -> URSHIFT;
                case "+" -> ADD;
                case "-" -> SUBTRACT;
                case "*" -> TIMES;
                case "/" -> DIV;
                case "%" -> MOD;
                default -> null;
            };
        }

        static final Operator LPAREN = null;

        final int precedence;
        final boolean rightAssociative = false;

        Operator(int precedence) {
            this.precedence = precedence;
        }

        abstract long apply(long a, long b);
    }

    private enum Sign {
        NEGATIVE, NOT;

        @Nullable
        static Sign of(char c) {
            return switch (c) {
                case '-' -> NEGATIVE;
                case '~' -> NOT;
                default -> null;
            };
        }
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

    private static final class SignStack {

        final Sign[] arr;
        int size = 0;

        @StringRes
        final int errorTitle;

        SignStack(int capacity, @StringRes int errorTitle) {
            arr = new Sign[capacity];
            this.errorTitle = errorTitle;
        }

        void push(Sign val) {
            arr[size++] = val;
        }

        Sign pop() {
            if (size < 1) throw malformedExpression(errorTitle);
            return arr[--size];
        }

        boolean notEmpty() {
            return size > 0;
        }
    }

    private static final class ValStack {

        final long[] vals;
        int size = 0;

        @StringRes
        final int errorTitle;

        ValStack(int capacity, @StringRes int errorTitle) {
            vals = new long[capacity];
            this.errorTitle = errorTitle;
        }

        void push(String operand) {
            vals[size] = tryParseLong(operand, errorTitle);
            ++size;
        }

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

        long value() {
            if (size != 1) throw malformedExpression(errorTitle);
            return vals[0];
        }
    }

    public static long compute(String expression, @StringRes int errorTitle) {
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
                    case '|', '^', '&', '+', '*', '/', '%' -> switch (prevType) {
                        case LPAREN, OPERATOR -> throw malformedExpression(errorTitle);
                        // todo: unary plus ugh.
                        case RPAREN, NUMBER -> extractChar(Type.OPERATOR);
                    };
                    case '<', '>' -> switch (prevType) {
                        case LPAREN, OPERATOR -> throw malformedExpression(errorTitle);
                        case RPAREN, NUMBER -> extractShift();
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
                        case OPERATOR -> extractSignedNumber(true);
                        case RPAREN, NUMBER -> extractChar(Type.OPERATOR);
                    };
                    case '~' -> switch (prevType) {
                        case LPAREN, OPERATOR -> extractSignedNumber(false);
                        case RPAREN -> phantomStar();
                        case NUMBER -> {
                            if (!isWhitespace(expr[pos - 1])) {
                                // require space for adjacency multiplication here.
                                throw malformedExpression(errorTitle);
                            }
                            yield phantomStar();
                        }
                    };
                    case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> switch (prevType) {
                        case RPAREN, NUMBER -> phantomStar();
                        // note that this treats space-separated numbers as multiplication.
                        case LPAREN, OPERATOR -> extractNumber();
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

            private String extractShift() {
                char shiftType = expr[pos];
                ++pos;
                if (!nextCharIs(shiftType)) throw malformedExpression(errorTitle);

                ++pos;
                String s;
                if (shiftType == '>' && nextCharIs('>')) {
                    s = ">>>";
                    advanceImmediate();
                } else {
                    s = Strings.repeat(shiftType, 2);
                    advance();
                }

                prevType = Type.OPERATOR;
                return s;
            }

            private boolean nextCharIs(char c) {
                return pos < length && expr[pos] == c;
            }

            private String phantomZero() {
                prevType = Type.NUMBER;
                return "0";
            }

            private String phantomStar() {
                prevType = Type.OPERATOR;
                return "*";
            }

            private void tryAdvanceImmediate() {
                do if (++pos == length) throw malformedExpression(errorTitle);
                while (isWhitespace(expr[pos]));
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

            private String extractNumber() {
                int start = pos;

                do ++pos;
                while (pos < length && isNumberChar(expr[pos]));

                var s = new String(Arrays.copyOfRange(expr, start, pos));
                advance();

                prevType = Type.NUMBER;
                return s;
            }

            private String extractSignedNumber(boolean negativeFirst) {
                var signs = new SignStack(length - pos, errorTitle);
                signs.push(negativeFirst ? Sign.NEGATIVE : Sign.NOT);

                do {
                    tryAdvanceImmediate();

                    var sign = Sign.of(expr[pos]);
                    if (sign == null) break;

                    signs.push(sign);
                } while (true);

                long n = tryParseLong(extractNumber(), errorTitle);
                do n = switch (signs.pop()) {
                    case NEGATIVE -> -n;
                    case NOT -> ~n;
                }; while (signs.notEmpty());

                return String.valueOf(n);
            }

            private static boolean isNumberChar(char c) {
                return switch (c) {
                    case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> true;
                    // note that unary minus isn't included for this method.
                    default -> false;
                };
            }
        }
    }

    private static long tryParseLong(String operand, int errorTitle) { try {
        return parseLong(operand);
    } catch (NumberFormatException e) {
        throw malformedExpression(errorTitle);
    }}

    private static EmillaException malformedExpression(@StringRes int errorTitle) {
        return new EmillaException(errorTitle, R.string.error_calc_malformed_expression);
    }

    private BitwiseCalculator() {}
}
