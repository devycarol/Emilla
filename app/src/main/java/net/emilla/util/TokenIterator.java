package net.emilla.util;

import java.util.Iterator;

public final class TokenIterator implements Iterator<String> {

    private final byte[] mText;
    private final byte mDelimiter;
    private final int mLength;

    private int mPosition = 0;

    public TokenIterator(byte[] bytes, byte delimiter) {
        mText = bytes;
        mDelimiter = delimiter;
        mLength = bytes.length;
    }

    @Override
    public boolean hasNext() {
        return mPosition < mLength;
    }

    @Override
    public String next() {
        int start = mPosition;
        int end = start;

        while (end < mLength && mText[end] != mDelimiter) {
            ++end;
        }

        mPosition = end + 1;

        int length = end - start;
        return new String(mText, start, length);
    }

}
