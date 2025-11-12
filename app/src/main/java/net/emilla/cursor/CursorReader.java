package net.emilla.cursor;

/*internal*/ abstract class CursorReader {

    /*internal*/ final String[] mProjection;

    /*internal*/ CursorReader(String[] projection) {
        mProjection = projection;
    }

}
