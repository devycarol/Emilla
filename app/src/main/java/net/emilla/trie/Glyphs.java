package net.emilla.trie;

import net.emilla.lang.Lang;

/*internal*/ final class Glyphs extends Phrase<Integer> {

    /*internal*/ Glyphs(String phrase) {
        super(phrase);
    }

    @Override
    protected Integer normalizedNext() {
        int codePoint = Character.codePointAt(mPhrase, mPosition);
        mPosition += Character.charCount(codePoint);
        return Lang.normalize(codePoint);
    }

}
