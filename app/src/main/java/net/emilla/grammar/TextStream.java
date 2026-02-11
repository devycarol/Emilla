package net.emilla.grammar;

import androidx.annotation.Nullable;

import net.emilla.util.Chars;

public final class TextStream {
    private final char[] mChars;
    private int mPosition = 0;

    public TextStream(String s) {
        mChars = s.toCharArray();
        passWhitespace();
    }

    public final class Bookmark {
        private final int mPosition;

        private Bookmark(int position) {
            mPosition = position;
        }

        private TextStream issuer() {
            return TextStream.this;
        }
    }

    public Bookmark position() {
        return new Bookmark(mPosition);
    }

    public void reset(Bookmark bookmark) {
        if (bookmark.issuer() != this) {
            throw new IllegalArgumentException("The bookmark isn't from this text stream");
        }

        mPosition = bookmark.mPosition;
    }

    public boolean skip(char c) {
        if (isFinished()) {
            return false;
        }
        if (c != mChars[mPosition]) {
            return false;
        }

        ++mPosition;
        passWhitespace();

        return true;
    }

    @Nullable
    public Integer integer() {
        int start = mPosition;
        if (hasRemaining() && Chars.isSign(mChars[mPosition])) {
            ++mPosition;
        }
        while (hasRemaining() && Character.isDigit(mChars[mPosition])) {
            ++mPosition;
        }
        passWhitespace();

        try {
            return Integer.parseInt(new String(mChars, start, mPosition - start));
        } catch (NumberFormatException e) {
            mPosition = start;
            return null;
        }
    }

    private void passWhitespace() {
        while (hasRemaining() && Character.isWhitespace(mChars[mPosition])) {
            ++mPosition;
        }
    }

    public boolean hasRemaining() {
        return mPosition < mChars.length;
    }

    private boolean isFinished() {
        return mPosition == mChars.length;
    }
}
