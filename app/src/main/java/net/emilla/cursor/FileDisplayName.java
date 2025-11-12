package net.emilla.cursor;

import android.database.Cursor;
import android.provider.DocumentsContract.Document;

public final class FileDisplayName extends CursorExtractor<String> {

    private static final int INDEX_DISPLAY_NAME = 0;

    private static final String[] PROJECTION = {
        Document.COLUMN_DISPLAY_NAME
    };

    public static final FileDisplayName INSTANCE = new FileDisplayName();

    private FileDisplayName() {
        super(PROJECTION);
    }

    @Override
    public String extract(Cursor cursor) {
        return cursor.getString(INDEX_DISPLAY_NAME);
    }

}
