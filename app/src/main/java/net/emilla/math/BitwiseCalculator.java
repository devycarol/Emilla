package net.emilla.math;

import androidx.annotation.StringRes;

import net.emilla.math.CalcToken.BitwiseToken;
import net.emilla.math.CalcToken.LParen;
import net.emilla.math.CalcToken.RParen;
import net.emilla.util.Strings;

import java.util.Arrays;
import java.util.Iterator;

public enum BitwiseCalculator {;
    public static long compute(String expression, @StringRes int errorTitle) {
        int len = expression.length();
        var operators = new EnumStack<BitwiseOperator>(len, BitwiseOperator::of, errorTitle);
        var result = new CalcStack<Long, BitwiseOperator, BitwiseSign>(
            len,
            BitwiseSign::of,
            Long[]::new,
            errorTitle
        );

        try {
            for (BitwiseToken token : new BitwiseTokens(expression, errorTitle)) {
                switch (token) {
                case BitwiseOperator op -> result.applyOperator(op, operators);
                case BitwiseSign op -> result.applySign(op);
                case LParen __ -> {
                    result.applyLParen();
                    operators.push(null);
                }
                case RParen __ -> result.applyRParen(operators);
                case IntegerNumber number -> result.push(number.value);
                }
            }

            while (operators.notEmpty()) {
                BitwiseOperator pop = operators.pop();
                if (pop != null) {
                    // not left paren
                    result.squish(pop);
                } else {
                    while (operators.notEmpty()) {
                        if (operators.peek() == null) {
                            // left paren
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

    private record BitwiseTokens(String expression, @StringRes int errorTitle)
        implements Iterable<BitwiseToken>
    {
        enum Type {
            LPAREN,
            RPAREN,
            OPERATOR,
            NUMBER,
        }

        @Override
        public Iterator<BitwiseToken> iterator() {
            return new BitwiseIterator(expression, errorTitle);
        }

        static final class BitwiseIterator implements Iterator<BitwiseToken> {
            final char[] expr;
            final int length;
            @StringRes
            final int errorTitle;
            int pos;
            Type prevType = Type.LPAREN;
            // an imaginary leading parenthesis gets us the behavior we want without worrying about
            // field nullity.

            BitwiseIterator(String expression, @StringRes int errorTitle) {
                this.expr = expression.toCharArray();
                this.length = this.expr.length;
                this.errorTitle = errorTitle;
                this.pos = Strings.indexOfNonSpace(this.expr);
            }

            @Override
            public boolean hasNext() {
                return this.pos < this.length;
            }

            @Override
            public BitwiseToken next() {
                return switch (this.expr[this.pos]) {
                    case '+', '-' -> switch (this.prevType) {
                        case LPAREN, OPERATOR -> extractUnary(false);
                        case RPAREN, NUMBER -> extractOperator();
                    };
                    case '~' -> switch (this.prevType) {
                        case LPAREN, OPERATOR -> extractUnary(false);
                        case RPAREN, NUMBER -> phantomStar();
                        // note that a binary-looking tilde is treated with adjacency multiplication.
                    };
                    case '!' -> switch (this.prevType) {
                        case LPAREN, OPERATOR -> throw Maths.malformedExpression(this.errorTitle);
                        case RPAREN, NUMBER -> extractUnary(true);
                    };
                    case '|', '^', '&', '*', '/', '%' -> switch (this.prevType) {
                        case LPAREN, OPERATOR -> throw Maths.malformedExpression(this.errorTitle);
                        case RPAREN, NUMBER -> extractOperator();
                    };
                    case '<', '>' -> switch (this.prevType) {
                        case LPAREN, OPERATOR -> throw Maths.malformedExpression(this.errorTitle);
                        case RPAREN, NUMBER -> extractShift();
                    };
                    case '(' -> switch (this.prevType) {
                        case LPAREN, OPERATOR -> extractLParen();
                        case RPAREN, NUMBER -> phantomStar();
                    };
                    case ')' -> switch (this.prevType) {
                        case LPAREN, OPERATOR -> throw Maths.malformedExpression(this.errorTitle);
                        case RPAREN, NUMBER -> extractRParen();
                    };
                    case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> switch (this.prevType) {
                        case LPAREN, OPERATOR -> extractNumber();
                        case RPAREN, NUMBER -> phantomStar();
                        // note that this treats space-separated numbers as multiplication.
                    };
                    default -> throw Maths.malformedExpression(this.errorTitle);
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
                var type = postfix
                    ? Type.NUMBER
                    : Type.OPERATOR
                ;
                // postfix unaries are applied immediately, so act like token was a number.
                return BitwiseSign.of(extractChar(type));
            }

            private BitwiseOperator extractOperator() {
                return BitwiseOperator.of(String.valueOf(extractChar(Type.OPERATOR)));
            }

            private char extractChar(Type type) {
                char c = this.expr[this.pos];
                advanceImmediate();
                this.prevType = type;
                return c;
            }

            private BitwiseOperator extractShift() {
                char shiftType = this.expr[this.pos];
                ++this.pos;
                if (!nextCharIs(shiftType)) {
                    throw Maths.malformedExpression(this.errorTitle);
                }

                ++this.pos;
                BitwiseOperator op;
                if (shiftType == '>' && nextCharIs('>')) {
                    op = BitwiseOperator.USHR;
                    advanceImmediate();
                } else {
                    op = BitwiseOperator.of(String.valueOf(shiftType) + shiftType);
                    advance();
                }

                this.prevType = Type.OPERATOR;
                return op;
            }

            private boolean nextCharIs(char c) {
                return this.pos < this.length && this.expr[this.pos] == c;
            }

            private BitwiseOperator phantomStar() {
                this.prevType = Type.OPERATOR;
                return BitwiseOperator.TIMES;
            }

            private void advanceImmediate() {
                do {
                    ++this.pos;
                } while (this.pos < this.length && Character.isWhitespace(this.expr[this.pos]));
            }

            private void advance() {
                while (this.pos < this.length && Character.isWhitespace(this.expr[this.pos])) {
                    ++this.pos;
                }
            }

            private IntegerNumber extractNumber() {
                int start = this.pos;

                do {
                    ++this.pos;
                } while (this.pos < this.length && isNumberChar(this.expr[this.pos]));

                var s = new String(Arrays.copyOfRange(this.expr, start, this.pos));
                advance();

                this.prevType = Type.NUMBER;
                return new IntegerNumber(s, this.errorTitle);
            }

            private static boolean isNumberChar(char c) {
                return switch (c) {
                    case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> true;
                    default -> false;
                };
            }
        }
    }
}
