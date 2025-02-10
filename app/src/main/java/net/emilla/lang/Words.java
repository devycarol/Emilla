package net.emilla.lang;

import static androidx.annotation.RestrictTo.Scope.SUBCLASSES;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import net.emilla.util.trie.TrieMap;

import java.util.Iterator;

/**
 * <p>
 * This class tokenizes strings into their constituent words, allowing iteration. Word-separation
 * varies by language.
 * </p>
 * <p>
 * The {@code Iterator.next()} methods always yield a lowercase string. This is to ensure
 * case-insensitivity in the TrieMap data structure, for which this class is a valid phrase.
 * </p>
 * <p>
 * Note: punctuations may be included in a "word". This construct is chiefly concerned with the
 * means of word-separation.
 * </p>
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
    public void setPosition(Iterator<String> iterator) {
        mPosition = ((WordIterator) iterator).mStartIndex;
    }

    @Override
    public boolean hasRemainingContents() {
        return mPosition < mPhrase.length();
    }

    @Override
    public String remainingContents() {
        return mPhrase.substring(mPosition);
    }

    private abstract class WordIterator implements Iterator<String> {

        int mStartIndex;

        private WordIterator(int startIndex) {
            mStartIndex = startIndex;
        }

        @Override
        public boolean hasNext() {
            return mStartIndex < mPhrase.length();
        }
    }

    static class Latin extends Words {

        Latin(String phrase) {
            super(phrase);
        }

        @Override @NonNull
        public Iterator<String> iterator() {
            return new LatinIterator();
        }

        private class LatinIterator extends WordIterator {

            private static int firstIndexOfNonSpace(String s) {
                if (s.isEmpty() || !Character.isWhitespace(s.charAt(0))) return 0;

                int index = 0, len = s.length();
                do if (++index >= len) return len;
                while (Character.isWhitespace(s.charAt(index)));

                return index;
            }

            private LatinIterator() {
                super(firstIndexOfNonSpace(mPhrase));
            }

            @Override
            public String next() {
                int endIndex = mStartIndex;
                int len = mPhrase.length();

                do if (++endIndex >= len) break;
                while (!Character.isWhitespace(mPhrase.charAt(endIndex)));
                final var word = mPhrase.substring(mStartIndex, endIndex);

                if (endIndex < len) {
                    do if (++endIndex >= len) break;
                    while (Character.isWhitespace(mPhrase.charAt(endIndex)));
                }
                mStartIndex = endIndex;

                return word.toLowerCase();
                // convert to lowercase to ensure TrieMap is case-insensitive
            }
        }
    }

    static class Glyph extends Words {

        Glyph(String phrase) {
            super(phrase);
        }

        @Override @NonNull
        public Iterator<String> iterator() {
            return new GlyphIterator();
        }

        private class GlyphIterator extends WordIterator {

            private GlyphIterator() {
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
