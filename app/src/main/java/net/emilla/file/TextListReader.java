package net.emilla.file;

import androidx.annotation.Nullable;

import net.emilla.annotation.internal;
import net.emilla.util.ArrayLoader;
import net.emilla.util.Chars;

final class TextListReader {

    private static final ListItem[] EMPTY_ARRAY = new ListItem[0];

    private final byte[] mText;
    private final int mLength;

    private int mPosition = 0;

    @internal TextListReader(byte[] bytes) {
        mText = bytes;
        mLength = bytes.length;
    }

    public ListItem[] toArray() {
        FileListItem firstItem = nextItem(0);
        if (firstItem == null) {
            return EMPTY_ARRAY;
        }

        var loader = new ArrayLoader<ListItem>(10, ListItem[]::new);
        loader.add(firstItem);

        do {
            FileListItem item = nextItem(-1);
            if (item == null) {
                break;
            }

            loader.growingAdd(item);
        } while (true);

        return loader.array();
    }

    @Nullable
    private FileListItem nextItem(int offsetLineCount) {
        if (mPosition == mLength) {
            return null;
        }

        do {
            boolean foundLine = false;
            if (mText[mPosition] == '\r') {
                ++mPosition;
                if (mPosition == mLength) {
                    return null;
                }
                foundLine = true;
            }
            // \r\n is considered one line
            if (mText[mPosition] == '\n') {
                ++mPosition;
                if (mPosition == mLength) {
                    return null;
                }
                foundLine = true;
            }

            if (foundLine) {
                ++offsetLineCount;
            } else {
                break;
            }
        } while (true);

        int start = mPosition;

        do ++mPosition;
        while (mPosition < mLength && !Chars.isLineSeparator(mText[mPosition]));

        int span = mPosition - start;

        return new FileListItem(mText, start, span, offsetLineCount);
    }

}
