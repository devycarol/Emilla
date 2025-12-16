package net.emilla.text;

import androidx.annotation.Nullable;

import net.emilla.util.ArrayLoader;

import java.util.NoSuchElementException;

public final class Csv {

    private final char[] mText;
    private final int mEnd;

    private int mPosition;

    /*internal*/ Csv(char[] text, int start, int span) {
        mText = text;
        mEnd = start + span;

        mPosition = start;
    }

    private boolean hasNext() {
        return mPosition < mEnd;
    }

    public String requireNext() {
        String next = next();
        if (next == null) {
            throw new NoSuchElementException("Null CSV value");
        }

        return next;
    }

    @Nullable
    public String next() {
        if (hasNext()) {
            return nextInternal();
        }

        throw endOfIterator();
    }

    @Nullable
    private String nextInternal() {
        var sb = new StringBuilder();

        boolean escape = false;
        while (mPosition < mEnd) {
            char c = mText[mPosition];
            switch (c) {
            case '\\' -> {
                if (escape) {
                    sb.append('\\');
                    escape = false;
                } else {
                    escape = true;
                }
            }
            case ',' -> {
                if (!escape) {
                    ++mPosition;
                    return extractString(sb);
                }

                sb.append(',');
                escape = false;
            }
            default -> {
                sb.append(c);
                escape = false;
            }}
            ++mPosition;
        }

        return extractString(sb);
    }

    @Nullable
    private static String extractString(CharSequence text) {
        return text.isEmpty()
            ? null
            : text.toString();
    }

    public String[] remainingValues() {
        if (!hasNext()) {
            throw endOfIterator();
        }

        var loader = new ArrayLoader<String>(1, String[]::new);

        do loader.growingAdd(nextInternal());
        while (hasNext());

        return loader.array();
    }

    private static NoSuchElementException endOfIterator() {
        return new NoSuchElementException("The CSV iterator has ended");
    }

}