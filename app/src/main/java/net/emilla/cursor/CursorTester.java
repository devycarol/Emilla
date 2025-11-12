package net.emilla.cursor;

import android.database.Cursor;

import java.util.function.Predicate;

public abstract class CursorTester extends CursorReader implements Predicate<Cursor> {

    /*internal*/ CursorTester(String[] projection) {
        super(projection);
    }

}
