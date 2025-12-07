package net.emilla.file;

import net.emilla.sort.IndexPortion;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public sealed class ListItem permits FileListItem {

    public final String text;
    private int mLeadingLineCount;

    private boolean mIsChecked = false;

    public ListItem(String text) {
        this(text, 0);
    }

    /*internal*/ ListItem(String text, int leadingLineCount) {
        this.text = text;
        mLeadingLineCount = leadingLineCount;
    }

    public final boolean isChecked() {
        return mIsChecked;
    }

    public final boolean toggle() {
        mIsChecked = !mIsChecked;
        return mIsChecked;
    }

    public static void rearrangeLeadingLines(ListItem[] list, IndexPortion[] sortedRemovePortions) {
        int last = sortedRemovePortions.length - 1;
        if (last < 0) {
            return;
        }

        if (sortedRemovePortions[last].nextIndex() >= list.length) {
            --last;
            if (last < 0) {
                return;
            }
        }

        for (int i = 0; i <= last; ++i) {
            IndexPortion portion = sortedRemovePortions[i];
            int leadingLineCount = list[portion.index].mLeadingLineCount;
            if (leadingLineCount == 0) {
                continue;
            }

            list[portion.nextIndex()].mLeadingLineCount = leadingLineCount;
        }
    }

    public final void writeTo(OutputStream ostream) throws IOException {
        for (int i = 0; i < mLeadingLineCount; ++i) {
            var emptyLines = new byte[mLeadingLineCount];
            Arrays.fill(emptyLines, (byte) '\n');
            ostream.write(emptyLines);
        }
        writeText(ostream);
    }

    protected /*open*/ void writeText(OutputStream ostream) throws IOException {
        ostream.write(this.text.getBytes());
    }

}
