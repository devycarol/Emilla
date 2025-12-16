package net.emilla.text;

import java.util.Iterator;

public final class CsvLines implements Iterator<Csv> {

    private final char[] mText;
    private final int mLength;

    private int mPosition = 0;

    public CsvLines(String lines) {
        mText = lines.toCharArray();
        mLength = mText.length;
    }

    @Override
    public boolean hasNext() {
        return mPosition < mLength;
    }

    @Override
    public Csv next() {
        int start = mPosition;

        do ++mPosition;
        while (mPosition < mLength && mText[mPosition] != '\n');

        int span = mPosition - start;
        return new Csv(mText, start, span);
    }

}
