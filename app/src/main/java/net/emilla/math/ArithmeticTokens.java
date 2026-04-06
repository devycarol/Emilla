package net.emilla.math;

import java.math.BigDecimal;
import java.util.Arrays;

final class ArithmeticTokens extends CalcTokens<ArithmeticToken> {
    ArithmeticTokens(String expression) {
        super(expression);
    }

    @Override
    public ArithmeticToken next() {
        return switch (expression[position]) {
            case '+', '-' -> switch (previousType) {
                case LPAREN, OPERATOR -> extractSign(false);
                case RPAREN, NUMBER -> extractOperator();
            };
            case '%', '!' -> switch (previousType) {
                case LPAREN, OPERATOR -> throw Maths.malformedExpression();
                case RPAREN, NUMBER -> extractSign(true);
            };
            case '*', '/', '^' -> switch (previousType) {
                case LPAREN, OPERATOR -> throw Maths.malformedExpression();
                case RPAREN, NUMBER -> extractOperator();
            };
            case '(' -> switch (previousType) {
                case LPAREN, OPERATOR -> extractLParen();
                case RPAREN, NUMBER -> phantomStar();
            };
            case ')' -> switch (previousType) {
                case LPAREN, OPERATOR -> throw Maths.malformedExpression();
                case RPAREN, NUMBER -> extractRParen();
            };
            case '.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> switch (previousType) {
                case LPAREN, OPERATOR -> extractNumber();
                case RPAREN, NUMBER -> phantomStar();
                // note that this treats space-separated numbers as multiplication.
            };
            default -> throw Maths.malformedExpression();
        };
    }

    private ArithmeticSign extractSign(boolean postfix) {
        var type = postfix
            ? TokenType.NUMBER
            // postfix unaries are applied immediately, so act like token was a number.
            : TokenType.OPERATOR
        ;
        return ArithmeticSign.of(extractChar(type));
    }

    private ArithmeticOperator extractOperator() {
        return ArithmeticOperator.of(extractChar(TokenType.OPERATOR));
    }

    private ArithmeticOperator phantomStar() {
        previousType = TokenType.OPERATOR;
        return ArithmeticOperator.MULTIPLY;
    }

    private FloatingPointNumber extractNumber() {
        int start = position;

        do {
            ++position;
        } while (position < length && isNumberChar(expression[position]));

        var s = new String(Arrays.copyOfRange(expression, start, position));
        advance();

        previousType = TokenType.NUMBER;
        return new FloatingPointNumber(new BigDecimal(s));
    }

    private static boolean isNumberChar(char c) {
        return c == '.' || Character.isDigit(c);
    }
}
