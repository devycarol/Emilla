package net.emilla.math;

import java.util.Iterator;

abstract class CalcTokens<T> implements Iterator<T> {
    protected final char[] expression;
    protected final int length;
    protected int position = 0;
    protected TokenType previousType = TokenType.LPAREN;
    // an imaginary leading parenthesis gets us the behavior we want without worrying about
    // field nullity.

    CalcTokens(String expression) {
        this.expression = expression.toCharArray();
        this.length = this.expression.length;
        advance();
    }

    protected final LParen extractLParen() {
        char __ = extractChar(TokenType.LPAREN);
        return LParen.INSTANCE;
    }

    protected final RParen extractRParen() {
        char __ = extractChar(TokenType.RPAREN);
        return RParen.INSTANCE;
    }

    protected final char extractChar(TokenType itsType) {
        char ch = expression[position];
        advanceImmediate();
        previousType = itsType;
        return ch;
    }

    protected final void advanceImmediate() {
        do {
            ++position;
        } while (position < length && Character.isWhitespace(expression[position]));
    }

    protected final void advance() {
        while (position < length && Character.isWhitespace(expression[position])) {
            ++position;
        }
    }

    @Override
    public final boolean hasNext() {
        return position < length;
    }
}
