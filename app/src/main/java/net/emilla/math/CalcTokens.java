package net.emilla.math;

import androidx.annotation.StringRes;

import net.emilla.util.Strings;

import java.util.Iterator;

abstract class CalcTokens
<
    T extends CalcToken,
    O extends CalcOperator<V>,
    S extends CalcSign<V>,
    V
>
    implements Iterator<T>
{
    protected final char[] expression;
    protected final int length;
    @StringRes
    protected final int errorTitle;
    protected int position;
    protected TokenType previousType = TokenType.LPAREN;
    // an imaginary leading parenthesis gets us the behavior we want without worrying about
    // field nullity.

    CalcTokens(String expression, @StringRes int errorTitle) {
        this.expression = expression.toCharArray();
        this.length = this.expression.length;
        this.errorTitle = errorTitle;
        this.position = Strings.indexOfNonSpace(this.expression);
    }

    protected abstract S extractSign(boolean postfix);

    protected abstract O extractOperator();

    protected abstract CalcValue<V> extractNumber();

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
