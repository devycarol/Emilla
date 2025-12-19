package net.emilla.cursor;

import android.database.Cursor;

import net.emilla.annotation.internal;

public abstract class CursorArrayExtractor<T>
    extends CursorExtractor<T> {

    @internal CursorArrayExtractor(String[] projection) {
        super(projection);
    }

    public abstract boolean filter(Cursor cursor);
    public abstract T[] newArray(int length);

}
