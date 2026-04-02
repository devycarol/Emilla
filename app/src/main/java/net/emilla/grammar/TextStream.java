package net.emilla.grammar;

import androidx.annotation.Nullable;

import net.emilla.annotation.Normalized;
import net.emilla.lang.Lang;
import net.emilla.util.Chars;
import net.emilla.util.DoubleFloat;
import net.emilla.util.Int;

import java.util.function.Function;

public final class TextStream {
    private final char[] mChars;
    private int mPosition = 0;

    public TextStream(String s) {
        mChars = s.toCharArray();
        boolean __ = passWhitespace();
    }

    @Nullable
    public static <T> T extract(String s, Function<TextStream, T> extractor) {
        var stream = new TextStream(s);

        T value = extractor.apply(stream);
        if (value != null && stream.isFinished()) {
            return value;
        }

        return null;
    }

    @Nullable @SafeVarargs
    public static <T> T extractFirst(String s, Function<TextStream, T>... candidates) {
        var stream = new TextStream(s);
        int start = stream.mPosition;
        for (Function<TextStream, T> extractor : candidates) {
            T value = extractor.apply(stream);
            if (value != null && stream.isFinished()) {
                return value;
            }

            stream.mPosition = start;
        }

        return null;
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

    private boolean different(@Normalized char ch) {
        return ch != Lang.normalize(mChars[mPosition]);
    }

    public boolean skip(@Normalized char ch) {
        if (isFinished() || different(ch)) {
            return false;
        }

        ++mPosition;
        boolean __ = passWhitespace();

        return true;
    }

    private boolean skip(@Normalized String s) {
        int start = mPosition;
        for (char ch : s.toCharArray()) {
            if (isFinished() || different(ch)) {
                mPosition = start;
                return false;
            }

            ++mPosition;
        }

        boolean __ = passWhitespace();
        return true;
    }

    public boolean skipFirst(@Normalized String... strings) {
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
    public Int integer() {
        int start = mPosition;
        if (hasRemaining() && Chars.isSign(mChars[mPosition])) {
            ++mPosition;
        }
        return unsignedInt(start);
    }

    @Nullable
    public Int unsignedInt() {
        return unsignedInt(mPosition);
    }

    @Nullable
    private Int unsignedInt(int start) {
        while (hasRemaining() && Character.isDigit(mChars[mPosition])) {
            ++mPosition;
        }
        boolean __ = passWhitespace();

        try {
            return new Int(Integer.parseInt(new String(mChars, start, mPosition - start)));
        } catch (NumberFormatException e) {
            mPosition = start;
            return null;
        }
    }

    @Nullable
    public DoubleFloat unsignedDouble() {
        int start = mPosition;
        while (hasRemaining() && Chars.isNumberChar(mChars[mPosition])) {
            ++mPosition;
        }
        boolean __ = passWhitespace();

        try {
            return new DoubleFloat(
                Double.parseDouble(new String(mChars, start, mPosition - start))
            );
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

    public boolean isAtWordStart() {
        return mPosition == 0 || Character.isWhitespace(mChars[mPosition - 1]);
    }

    public boolean hasRemaining() {
        return mPosition != mChars.length;
    }

    public boolean isFinished() {
        return mPosition == mChars.length;
    }
}
