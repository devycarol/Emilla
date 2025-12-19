package net.emilla.trie;

import net.emilla.annotation.internal;
import net.emilla.lang.Lang;

final class Words extends Phrase<String> {

    @internal Words(String phrase) {
        super(phrase);
    }

    @Override
    protected String normalizedNext() {
        int start = mPosition;

        do ++mPosition;
        while (mPosition < mLength && !Character.isWhitespace(mPhrase[mPosition]));

        int span = mPosition - start;
        var word = new String(mPhrase, start, span);

        return Lang.normalize(word);
    }

}
