package net.emilla.lang;

public final class Word extends LatinToken {
    private final String mWord;

    public Word(boolean requireSpaceBefore, String word, boolean ignoreCase) {
        super(requireSpaceBefore, ignoreCase);
        mWord = word;
    }

    public int length() {
        return mWord.length();
    }

    public boolean matches(String s) {
        return ignoreCase
            ? s.equalsIgnoreCase(mWord)
            : s.equals(mWord)
        ;
    }
}
