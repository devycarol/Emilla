package net.emilla.trie;

import androidx.annotation.Nullable;

import net.emilla.annotation.internal;

abstract class Phrase<W> implements PositionalIterator<W, String> {

    @internal final char[] mPhrase;
    @internal final int mLength;

    @internal int mPosition = 0;

    @internal Phrase(String phrase) {
        mPhrase = phrase.toCharArray();
        mLength = mPhrase.length;

        passWhitespace();
    }

    protected abstract W normalizedNext();

    private void passWhitespace() {
        while (mPosition < mLength && Character.isWhitespace(mPhrase[mPosition])) {
            ++mPosition;
        }
    }

    @Override
    public final boolean hasNext() {
        return mPosition < mLength;
    }

    @Override
    public final W next() {
        // behavior when hasNext() -> false is undefined
        W next = normalizedNext();
        passWhitespace();
        return next;
    }

    @Override
    public final int position() {
        return mPosition;
    }

    @Override @Nullable
    public final String leftoversFrom(int position) {
        if (position == mLength) {
            return null;
        }
        int span = mLength - position;
        return new String(mPhrase, position, span);
    }

}
