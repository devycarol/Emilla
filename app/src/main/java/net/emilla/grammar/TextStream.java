package net.emilla.grammar;

import androidx.annotation.Nullable;

import net.emilla.util.Chars;

import java.util.function.Function;

public final class TextStream {
    private final char[] mChars;
    private int mPosition = 0;

    public TextStream(String s) {
        mChars = s.toCharArray();
        boolean __ = passWhitespace();
    }

    @Nullable
    public <T> T extract(Function<TextStream, T> extractor) {
        int start = mPosition;

        T value = extractor.apply(this);
        if (value == null) {
            mPosition = start;
        }

        return value;
    }

    @Nullable
    public static <T> T extract(String s, Function<TextStream, T> extractor) {
        var stream = new TextStream(s);
        int start = stream.mPosition;

        T value = extractor.apply(stream);
        if (stream.hasRemaining()) {
            value = null;
        }
        if (value == null) {
            stream.mPosition = start;
        }

        return value;
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

    public boolean skip(char ch) {
        if (isFinished() || ch != mChars[mPosition]) {
            return false;
        }

        ++mPosition;
        boolean __ = passWhitespace();

        return true;
    }

    private boolean skip(String s) {
        int start = mPosition;
        for (char ch : s.toCharArray()) {
            if (isFinished() || ch != mChars[mPosition]) {
                mPosition = start;
                return false;
            }
            ++mPosition;
        }

        boolean __ = passWhitespace();
        return true;
    }

    public boolean skipFirst(String... strings) {
        for (String s : strings) {
            if (skip(s)) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    public String token(CharPredicate condition) {
        int start = mPosition;
        while (hasRemaining() && condition.test(mChars[mPosition])) {
            ++mPosition;
        }

        if (start == mPosition) {
            return null;
        }

        boolean __ = passWhitespace();
        return new String(mChars, start, mPosition - start);
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
        boolean __ = passWhitespace();

        try {
            return Integer.parseInt(new String(mChars, start, mPosition - start));
        } catch (NumberFormatException e) {
            mPosition = start;
            return null;
        }
    }

    private boolean passWhitespace() {
        if (isFinished() || !Character.isWhitespace(mChars[mPosition])) {
            return false;
        }

        do {
            ++mPosition;
        } while (hasRemaining() && Character.isWhitespace(mChars[mPosition]));

        return true;
    }

    public boolean hasRemaining() {
        return mPosition < mChars.length;
    }

    private boolean isFinished() {
        return mPosition == mChars.length;
    }
}
