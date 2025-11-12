package net.emilla.cursor;

import android.database.Cursor;
import android.provider.DocumentsContract.Document;

public final class IsWritableDirectory extends CursorTester {

    private static final int INDEX_MIME_TYPE = 0;
    private static final int INDEX_FLAGS = 1;

    private static final String[] PROJECTION = {
        Document.COLUMN_MIME_TYPE,
        Document.COLUMN_FLAGS,
    };

    private static final int REQUIRED_FLAGS
        = Document.FLAG_DIR_SUPPORTS_CREATE;
    private static final int MONITORED_FLAGS
        = REQUIRED_FLAGS
        | Document.FLAG_PARTIAL;

    public static final IsWritableDirectory INSTANCE = new IsWritableDirectory();

    private IsWritableDirectory() {
        super(PROJECTION);
    }

    @Override
    public boolean test(Cursor cursor) {
        return Document.MIME_TYPE_DIR.equals(cursor.getString(INDEX_MIME_TYPE))
            && (MONITORED_FLAGS & cursor.getInt(INDEX_FLAGS)) == REQUIRED_FLAGS;
    }

}
