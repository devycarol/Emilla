package net.emilla.cursor;

abstract class CursorReader {

    /*internal*/ final String[] mProjection;

    /*internal*/ CursorReader(String[] projection) {
        mProjection = projection;
    }

}
