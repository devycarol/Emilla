package net.emilla.lang;

import static androidx.annotation.RestrictTo.Scope.SUBCLASSES;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import net.emilla.util.Strings;
import net.emilla.util.trie.TrieMap;

import java.util.Iterator;

/**
 * <p>
 * This class tokenizes strings into their constituent words, allowing iteration. Word-separation
 * varies by language.</p>
 * <p>
 * The {@code Iterator.next()} methods always yield a lowercase string. This is to ensure
 * case-insensitivity in the TrieMap data structure, for which this class is a valid phrase.</p>
 * <p>
 * Note: punctuations may be included in a "word". This construct is chiefly concerned with the
 * means of word-separation.</p>
 */
public abstract class Words implements TrieMap.Phrase<String, String> {

    @RestrictTo(SUBCLASSES)
    final String mPhrase;
    @RestrictTo(SUBCLASSES)
    int mPosition = 0;

    private Words(String phrase) {
        mPhrase = phrase;
    }

    @Override
    public final void setPosition(Iterator<String> iterator) {
        mPosition = ((WordIterator) iterator).mStartIndex;
    }

    @Override
    public final boolean hasRemainingContents() {
        return mPosition < mPhrase.length();
    }

    @Override
    public final String remainingContents() {
        return mPhrase.substring(mPosition);
    }

    private abstract class WordIterator implements Iterator<String> {

        int mStartIndex;

        WordIterator(int startIndex) {
            mStartIndex = startIndex;
        }

        @Override
        public final boolean hasNext() {
            return mStartIndex < mPhrase.length();
        }
    }

    /*internal*/ static final class Latin extends Words {

        public Latin(String phrase) {
            super(phrase);
        }

        @Override @NonNull
        public Iterator<String> iterator() {
            return new LatinIterator();
        }

        private final class LatinIterator extends WordIterator {

            LatinIterator() {
                super(Strings.indexOfNonSpace(mPhrase));
            }

            @Override
            public String next() {
                int endIndex = mStartIndex;
                int len = mPhrase.length();

                do {
                    ++endIndex;
                    if (endIndex >= len) break;
                }
                while (!Character.isWhitespace(mPhrase.charAt(endIndex)));
                var word = mPhrase.substring(mStartIndex, endIndex);

                if (endIndex < len) {
                    do {
                        ++endIndex;
                        if (endIndex >= len) break;
                    }
                    while (Character.isWhitespace(mPhrase.charAt(endIndex)));
                }
                mStartIndex = endIndex;

                return word.toLowerCase();
                // convert to lowercase to ensure TrieMap is case-insensitive
            }
        }
    }

    /*internal*/ static final class Glyph extends Words {

        public Glyph(String phrase) {
            super(phrase);
        }

        @Override @NonNull
        public Iterator<String> iterator() {
            return new GlyphIterator();
        }

        private final class GlyphIterator extends WordIterator {

            GlyphIterator() {
                super(0);
            }

            @Override
            public String next() {
                int codePoint = mPhrase.codePointAt(mStartIndex);
                mStartIndex += Character.charCount(codePoint);
                return new String(Character.toChars(codePoint)).toLowerCase();
                // convert to lowercase to ensure TrieMap is case-insensitive
            }
        }
    }
}
