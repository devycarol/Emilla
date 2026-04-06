package net.emilla.math;

import net.emilla.util.Strings;

import java.util.Iterator;

abstract class CalcTokens<T> implements Iterator<T> {
    protected final char[] expression;
    protected final int length;
    protected int position;
    protected TokenType previousType = TokenType.LPAREN;
    // an imaginary leading parenthesis gets us the behavior we want without worrying about
    // field nullity.

    CalcTokens(String expression) {
        this.expression = expression.toCharArray();
        this.length = this.expression.length;
        this.position = Strings.indexOfNonSpace(this.expression);
    }

    protected final LParen extractLParen() {
        extractChar(TokenType.LPAREN);
        return LParen.INSTANCE;
    }

    protected final RParen extractRParen() {
        extractChar(TokenType.RPAREN);
        return RParen.INSTANCE;
    }

    protected final char extractChar(TokenType itsType) {
        char c = expression[position];
        advanceImmediate();
        previousType = itsType;
        return c;
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
