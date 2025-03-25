package net.emilla.math;

import static net.emilla.math.Maths.malformedExpression;
import static java.lang.Character.isWhitespace;
import static java.lang.Long.parseLong;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.math.CalcToken.LParen;
import net.emilla.math.CalcToken.RParen;
import net.emilla.util.Strings;

import java.util.Arrays;
import java.util.Iterator;

public final class BitwiseCalculator {

    /*internal*/ enum BitwiseOperator implements BitwiseToken {
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

        private static BitwiseOperator of(String token) {
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
                default -> throw new IllegalArgumentException();
            };
        }

        private static final BitwiseOperator LPAREN = null;

        private final int precedence;
        private final boolean rightAssociative = false;

        BitwiseOperator(int precedence) {
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

        void push(long operand) {
            vals[size] = operand;
            ++size;
        }

        void squish(BitwiseOperator op) {
            if (size < 2) throw malformedExpression(errorTitle);

            --size;
            vals[size - 1] = op.apply(vals[size - 1], vals[size]);
        }

        void applyOperator(BitwiseOperator op, OpStack opStk) {
            while (opStk.notEmpty()) {
                BitwiseOperator peek = opStk.peek();
                if (peek == BitwiseOperator.LPAREN || op.precedence > peek.precedence
                ||  op.rightAssociative && op.precedence == peek.precedence) break;
                else squish(opStk.pop()); // peek is valid, therefore pop is valid.
            }
            opStk.push(op);
        }

        void applyRParen(OpStack opStk) {
            while (opStk.notEmpty()) {
                BitwiseOperator pop = opStk.pop();
                if (pop == BitwiseOperator.LPAREN) break;
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

        for (BitwiseToken token : new BitwiseTokens(expression, errorTitle)) {
            if (token instanceof BitwiseOperator op) {
                result.applyOperator(op, opStk);
            } else if (token instanceof LParen) {
                opStk.push(BitwiseOperator.LPAREN);
            } else if (token instanceof RParen) {
                result.applyRParen(opStk);
            } else if (token instanceof IntegerNumber num) {
                result.push(num.val);
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

        static class BitwiseIterator implements Iterator<BitwiseToken> {

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
                    case '|', '^', '&', '+', '*', '/', '%' -> switch (prevType) {
                        case LPAREN, OPERATOR -> throw malformedExpression(errorTitle);
                        // todo: unary plus ugh.
                        case RPAREN, NUMBER -> extractOperator();
                    };
                    case '<', '>' -> switch (prevType) {
                        case LPAREN, OPERATOR -> throw malformedExpression(errorTitle);
                        case RPAREN, NUMBER -> extractShift();
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
                        case OPERATOR -> extractSignedNumber(true);
                        case RPAREN, NUMBER -> extractOperator();
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

            private LParen extractLParen() {
                extractChar(Type.LPAREN);
                return LParen.INSTANCE;
            }

            private RParen extractRParen() {
                extractChar(Type.RPAREN);
                return RParen.INSTANCE;
            }

            private BitwiseOperator extractOperator() {
                return BitwiseOperator.of(extractChar(Type.OPERATOR));
            }

            private String extractChar(Type type) {
                var s = String.valueOf(expr[pos]);
                advanceImmediate();
                prevType = type;
                return s;
            }

            private BitwiseOperator extractShift() {
                char shiftType = expr[pos];
                ++pos;
                if (!nextCharIs(shiftType)) throw malformedExpression(errorTitle);

                ++pos;
                BitwiseOperator op;
                if (shiftType == '>' && nextCharIs('>')) {
                    op = BitwiseOperator.URSHIFT;
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

            private IntegerNumber phantomZero() {
                prevType = Type.NUMBER;
                return new IntegerNumber(0);
            }

            private BitwiseOperator phantomStar() {
                prevType = Type.OPERATOR;
                return BitwiseOperator.TIMES;
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

            private IntegerNumber extractNumber() {
                int start = pos;

                do ++pos;
                while (pos < length && isNumberChar(expr[pos]));

                var s = new String(Arrays.copyOfRange(expr, start, pos));
                advance();

                prevType = Type.NUMBER;
                return new IntegerNumber(tryParseLong(s, errorTitle));
            }

            private IntegerNumber extractSignedNumber(boolean negativeFirst) {
                var signs = new SignStack(length - pos, errorTitle);
                signs.push(negativeFirst ? Sign.NEGATIVE : Sign.NOT);

                do {
                    tryAdvanceImmediate();

                    var sign = Sign.of(expr[pos]);
                    if (sign == null) break;

                    signs.push(sign);
                } while (true);

                IntegerNumber num = extractNumber();
                do num = switch (signs.pop()) {
                    case NEGATIVE -> new IntegerNumber(-num.val);
                    case NOT -> new IntegerNumber(~num.val);
                }; while (signs.notEmpty());

                return num;
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

    private BitwiseCalculator() {}
}
