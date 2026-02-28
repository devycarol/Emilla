package net.emilla.math;

import androidx.annotation.StringRes;

import net.emilla.math.CalcToken.InfixToken;
import net.emilla.math.CalcToken.LParen;
import net.emilla.math.CalcToken.RParen;
import net.emilla.util.Strings;

import java.util.Arrays;
import java.util.Iterator;

public enum Calculator {;
    public static double compute(String expression, @StringRes int errorTitle) {
        // Todo: use big decimals to reduce overflow, esp. in operations like factorial.

        int len = expression.length();
        var operators = new EnumStack<ArithmeticOperator>(len,  ArithmeticOperator::of, errorTitle);
        var result = new CalcStack<Double, ArithmeticOperator, ArithmeticSign>(
            len,
            ArithmeticSign::of,
            Double[]::new,
            errorTitle
        );

        try {
            for (InfixToken token : new InfixTokens(expression, errorTitle)) {
                switch (token) {
                case ArithmeticOperator op -> result.applyOperator(op, operators);
                case ArithmeticSign sign -> result.applySign(sign);
                case LParen __ -> {
                    result.applyLParen();
                    operators.push(null);
                }
                case RParen __ -> result.applyRParen(operators);
                case FloatingPointNumber number -> result.push(number.value);
                }
            }

            while (operators.notEmpty()) {
                ArithmeticOperator pop = operators.pop();
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

            private ArithmeticSign extractUnary(boolean postfix) {
                var type = postfix
                    ? Type.NUMBER
                    : Type.OPERATOR
                ;
                // postfix unaries are applied immediately, so act like token was a number.
                return ArithmeticSign.of(extractChar(type));
            }

            private ArithmeticOperator extractOperator() {
                return ArithmeticOperator.of(extractChar(Type.OPERATOR));
            }

            private char extractChar(Type type) {
                char c = expr[pos];
                advanceImmediate();
                prevType = type;
                return c;
            }

            private ArithmeticOperator phantomStar() {
                prevType = Type.OPERATOR;
                return ArithmeticOperator.TIMES;
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
