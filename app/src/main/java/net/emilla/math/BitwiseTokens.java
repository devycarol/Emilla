package net.emilla.math;

import java.math.BigInteger;
import java.util.Arrays;

final class BitwiseTokens extends CalcTokens<BitwiseToken> {
    BitwiseTokens(String expression) {
        super(expression);
    }

    @Override
    public BitwiseToken next() {
        return switch (expression[position]) {
            case '+', '-' -> switch (previousType) {
                case LPAREN, OPERATOR -> extractSign(false);
                case RPAREN, NUMBER -> extractOperator();
            };
            case '~' -> switch (previousType) {
                case LPAREN, OPERATOR -> extractSign(false);
                case RPAREN, NUMBER -> phantomStar();
                // note that a binary-looking tilde is treated with adjacency multiplication.
            };
            case '!' -> switch (previousType) {
                case LPAREN, OPERATOR -> throw Maths.malformedExpression();
                case RPAREN, NUMBER -> extractSign(true);
            };
            case '|', '^', '&', '*', '/', '%' -> switch (previousType) {
                case LPAREN, OPERATOR -> throw Maths.malformedExpression();
                case RPAREN, NUMBER -> extractOperator();
            };
            case '<', '>' -> switch (previousType) {
                case LPAREN, OPERATOR -> throw Maths.malformedExpression();
                case RPAREN, NUMBER -> extractShift();
            };
            case '(' -> switch (previousType) {
                case LPAREN, OPERATOR -> extractLParen();
                case RPAREN, NUMBER -> phantomStar();
            };
            case ')' -> switch (previousType) {
                case LPAREN, OPERATOR -> throw Maths.malformedExpression();
                case RPAREN, NUMBER -> extractRParen();
            };
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> switch (previousType) {
                case LPAREN, OPERATOR -> extractNumber();
                case RPAREN, NUMBER -> phantomStar();
                // note that this treats space-separated numbers as multiplication.
            };
            default -> throw Maths.malformedExpression();
        };
    }

    private BitwiseSign extractSign(boolean postfix) {
        var type = postfix
            ? TokenType.NUMBER
            // postfix unaries are applied immediately, so act like token was a number.
            : TokenType.OPERATOR
        ;
        return BitwiseSign.of(extractChar(type));
    }

    private BitwiseOperator extractOperator() {
        return BitwiseOperator.of(extractChar(TokenType.OPERATOR));
    }

    private BitwiseOperator extractShift() {
        char shiftType = expression[position];
        ++position;
        if (!nextCharIs(shiftType)) {
            throw Maths.malformedExpression();
        }

        advanceImmediate();
        previousType = TokenType.OPERATOR;

        return BitwiseOperator.of(shiftType);
    }

    private boolean nextCharIs(char c) {
        return position < length && expression[position] == c;
    }

    private BitwiseOperator phantomStar() {
        previousType = TokenType.OPERATOR;
        return BitwiseOperator.MULTIPLY;
    }

    private IntegerNumber extractNumber() {
        int start = position;

        do {
            ++position;
        } while (position < length && Character.isDigit(expression[position]));

        var s = new String(Arrays.copyOfRange(expression, start, position));
        advance();

        previousType = TokenType.NUMBER;
        return new IntegerNumber(new BigInteger(s));
    }
}
