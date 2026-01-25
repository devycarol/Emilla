package net.emilla.lang;

import androidx.annotation.Nullable;

import net.emilla.function.CharPredicate;
import net.emilla.util.Chars;
import net.emilla.util.Strings;

public final class LatinTokens {
    private final char[] mChars;
    private final int mLength;
    private int mPosition;

    public LatinTokens(String s) {
        mChars = s.toCharArray();
        mLength = mChars.length;
        mPosition = Strings.indexOfNonSpace(mChars);
    }

    public boolean hasNext() {
        return mPosition < mLength;
    }

    public boolean finished() {
        return !hasNext();
    }

    private void ensureNext() {
        if (finished()) {
            throw new IllegalStateException("The token iterator is finished");
        }
    }

    private void ensureNext(CharPredicate condition) {
        ensureNext();

        if (!condition.test(mChars[mPosition])) {
            throw new IllegalStateException("The next character doesn't satisfy the condition");
        }
    }

    public char peek() {
        ensureNext();
        return mChars[mPosition];
    }

    public char nextChar() {
        ensureNext();
        char ch = mChars[mPosition];

        advanceImmediate();
        return ch;
    }

    public String nextToken() {
        int start = scan(ch -> !Character.isWhitespace(ch));
        String token = Strings.substring(mChars, start, mPosition);

        advanceImmediate();
        return token;
    }

    public String nextWord() {
        int start = scan(Character::isLetter);
        String word = Strings.substring(mChars, start, mPosition);

        advance();
        return word;
    }

    public long nextInteger() {
        int start = scan(Chars::isSignOrDigit, Character::isDigit);
        String integer = Strings.substring(mChars, start, mPosition);

        advance();

        try {
            return Long.parseLong(integer);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid integer", e);
        }
    }

    public double nextNumber() {
        int start = scan(Chars::isSignOrNumberChar, Chars::isNumberChar);
        String number = Strings.substring(mChars, start, mPosition);

        advance();

        try {
            return Double.parseDouble(number);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid number", e);
        }
    }

    public void skipFirst(LatinToken... tokens) {
        if (hasNext()) {
            String __ = nextOfInternal(tokens);
        }
    }

    public String nextOf(LatinToken... tokens) {
        ensureNext();

        String next = nextOfInternal(tokens);
        if (next == null) {
            throw new IllegalStateException("A matching token wasn't found");
        }

        return next;
    }

    @Nullable
    private String nextOfInternal(LatinToken... tokens) {
        for (LatinToken token : tokens) {
            switch (token) {
            case Letter letter -> {
                char ch = mChars[mPosition];
                if (letter.matches(ch)) {
                    if (letter.mRequireSpaceBefore) {
                        ensureSpaceBefore();
                    }

                    advanceImmediate();
                    return String.valueOf(ch);
                }
            }
            case Word word -> {
                int end = mPosition + word.length();
                if (end > mLength) {
                    continue;
                }

                String sector = Strings.substring(mChars, mPosition, end);
                if (word.matches(sector)) {
                    if (word.mRequireSpaceBefore) {
                        ensureSpaceBefore();
                    }

                    advanceFrom(end);
                    return sector;
                }
            }}
        }

        return null;
    }

    private void ensureSpaceBefore() {
        if (!Character.isWhitespace(mChars[mPosition - 1])) {
            throw new IllegalStateException("The token iterator isn't positioned after a space");
        }
    }

    private int scan(CharPredicate condition) {
        return scan(condition, condition);
    }

    private int scan(CharPredicate precondition, CharPredicate condition) {
        ensureNext(precondition);
        int start = mPosition;

        do ++mPosition;
        while (mPosition < mLength && condition.test(mChars[mPosition]));

        return start;
    }

    private void advanceImmediate() {
        do ++mPosition;
        while (mPosition < mLength && Character.isWhitespace(mChars[mPosition]));
    }

    private void advance() {
        while (mPosition < mLength && Character.isWhitespace(mChars[mPosition])) {
            ++mPosition;
        }
    }

    private void advanceFrom(int start) {
        mPosition = start;
        advance();
    }

    public void requireFinished() {
        if (hasNext()) {
            throw new IllegalStateException("The token iterator isn't finished");
        }
    }
}
