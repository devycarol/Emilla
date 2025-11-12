package net.emilla.cursor;

import android.database.Cursor;
import android.provider.DocumentsContract.Document;

import net.emilla.file.TreeFile;
import net.emilla.util.MimeTypes;

public final class TextFiles extends CursorArrayExtractor<TreeFile> {

    private static final int INDEX_DOCUMENT_ID = 0;
    private static final int INDEX_MIME_TYPE = 1;
    private static final int INDEX_DISPLAY_NAME = 2;
    private static final int INDEX_FLAGS = 3;

    private static final String[] PROJECTION = {
        Document.COLUMN_DOCUMENT_ID,
        Document.COLUMN_MIME_TYPE,
        Document.COLUMN_DISPLAY_NAME,
        Document.COLUMN_FLAGS,
    };

    private static final int REQUIRED_FLAGS
        = Document.FLAG_SUPPORTS_WRITE;
    private static final int MONITORED_FLAGS
        = REQUIRED_FLAGS
        | Document.FLAG_PARTIAL;

    public static final TextFiles INSTANCE = new TextFiles();

    private TextFiles() {
        super(PROJECTION);
    }

    @Override
    public boolean filter(Cursor cursor) {
        return cursor.getString(INDEX_MIME_TYPE).startsWith(MimeTypes.PREFIX_TEXT)
            && (cursor.getInt(INDEX_FLAGS) & MONITORED_FLAGS) == REQUIRED_FLAGS;
    }

    @Override
    public TreeFile extract(Cursor cursor) {
        return new TreeFile(
            cursor.getString(INDEX_DOCUMENT_ID),
            cursor.getString(INDEX_MIME_TYPE),
            cursor.getString(INDEX_DISPLAY_NAME)
        );
    }

    @Override
    public TreeFile[] newArray(int length) {
        return new TreeFile[length];
    }

}
