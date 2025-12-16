package net.emilla.file;

import java.io.IOException;
import java.io.OutputStream;

final class FileListItem extends ListItem {

    private final byte[] mMother;
    private final int mPosition;
    private final int mSpan;

    /*internal*/ FileListItem(byte[] mother, int position, int span, int leadingLineCount) {
        super(new String(mother, position, span), leadingLineCount);

        mMother = mother;
        mPosition = position;
        mSpan = span;
    }

    @Override
    protected void writeText(OutputStream ostream) throws IOException {
        ostream.write(mMother, mPosition, mSpan);
    }

}
