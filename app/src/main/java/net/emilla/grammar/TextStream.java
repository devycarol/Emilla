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

    private TextStream(String s) {
        mChars = s.toCharArray();
        boolean __ = passWhitespace();
    }

    @Nullable
    public static <T> T extract(String s, Function<TextStream, T> extractor) {
        var stream = new TextStream(s);

        T value = extractor.apply(stream);
        if (stream.hasRemaining()) {
            return null;
        }

        return value;
    }

    @Nullable @SafeVarargs
    public static <T> T extractFirst(String s, Function<TextStream, T>... candidates) {
        var stream = new TextStream(s);
        for (Function<TextStream, T> extractor : candidates) {
            T value = stream.extract(extractor);
            if (value != null) {
                return value;
            }
        }

        return null;
    }

    @Nullable
    private <T> T extract(Function<TextStream, T> extractor) {
        int start = mPosition;

        T value = extractor.apply(this);
        if (value == null || hasRemaining()) {
            mPosition = start;
            return null;
        }

        return value;
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

    public boolean skip(@Normalized String s) {
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

    private void passSign() {
        if (hasRemaining() && Chars.isSign(mChars[mPosition])) {
            ++mPosition;
        }
    }

    @Nullable
    public Int integer() {
        int start = mPosition;
        passSign();
        return finishInt(start);
    }

    @Nullable
    public Int unsignedInt() {
        return finishInt(mPosition);
    }

    @Nullable
    private Int finishInt(int start) {
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
    public DoubleFloat doubleFloat() {
        int start = mPosition;
        passSign();
        return finishDouble(start);
    }

    @Nullable
    public DoubleFloat unsignedDouble() {
        int start = mPosition;
        return finishDouble(start);
    }

    @Nullable
    private DoubleFloat finishDouble(int start) {
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
