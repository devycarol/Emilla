package net.emilla.lang;

import static androidx.annotation.RestrictTo.Scope.SUBCLASSES;
import static java.lang.Character.isWhitespace;

import androidx.annotation.RestrictTo;

import net.emilla.struct.trie.TrieMap;
import net.emilla.util.Strings;

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
    final char[] mPhrase;
    @RestrictTo(SUBCLASSES)
    final int mLength;

    private int mPosition = 0;

    private Words(String phrase) {
        mPhrase = phrase.toCharArray();
        mLength = mPhrase.length;
    }

    @Override
    public final void setPosition(Iterator<String> iterator) {
        mPosition = ((WordIterator) iterator).mPos;
    }

    @Override
    public final boolean hasRemainingContents() {
        return mPosition < mLength;
    }

    @Override
    public final String remainingContents() {
        return Strings.substring(mPhrase, mPosition);
    }

    private /*inner*/ abstract class WordIterator implements Iterator<String> {

        int mPos = Strings.indexOfNonSpace(mPhrase);

        @Override
        public final boolean hasNext() {
            return mPos < mLength;
        }
    }

    /*internal*/ static final class Latin extends Words {

        public Latin(String phrase) {
            super(phrase);
        }

        @Override
        public Iterator<String> iterator() {
            return new LatinIterator();
        }

        private /*inner*/ final class LatinIterator extends WordIterator {

            @Override
            public String next() {
                int start = mPos;

                do if (++mPos == mLength) break;
                while (!isWhitespace(mPhrase[mPos]));

                String word = Strings.substring(mPhrase, start, mPos);
                advance();

                return word.toLowerCase();
                // convert to lowercase to ensure TrieMap is case-insensitive
            }

            private void advance() {
                while (mPos < mLength && isWhitespace(mPhrase[mPos])) {
                    ++mPos;
                }
            }
        }
    }

    /*internal*/ static final class Glyph extends Words {

        public Glyph(String phrase) {
            super(phrase);
        }

        @Override
        public Iterator<String> iterator() {
            return new GlyphIterator();
        }

        private /*inner*/ final class GlyphIterator extends WordIterator {

            @Override
            public String next() {
                int codePoint = Character.codePointAt(mPhrase, mPos);
                mPos += Character.charCount(codePoint);
                return new String(Character.toChars(codePoint)).toLowerCase();
                // convert to lowercase to ensure TrieMap is case-insensitive
            }
        }
    }
}
