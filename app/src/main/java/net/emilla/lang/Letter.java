package net.emilla.lang;

import net.emilla.util.Chars;

public final class Letter extends LatinToken {

    private final char mLetter;

    public Letter(boolean requireSpaceBefore, char letter, boolean ignoreCase) {
        super(requireSpaceBefore, ignoreCase);
        mLetter = letter;
    }

    public boolean matches(char ch) {
        return ignoreCase
            ? Chars.sameLetter(mLetter, ch)
            : mLetter == ch;
    }

}
