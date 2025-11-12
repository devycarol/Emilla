package net.emilla.cursor;

import android.database.Cursor;

public abstract class CursorExtractor<T> extends CursorReader {

    /*internal*/ CursorExtractor(String[] projection) {
        super(projection);
    }

    public abstract T extract(Cursor cursor);

}
