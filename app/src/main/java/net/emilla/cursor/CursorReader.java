package net.emilla.cursor;

import net.emilla.annotation.internal;

abstract class CursorReader {

    @internal final String[] mProjection;

    @internal CursorReader(String[] projection) {
        mProjection = projection;
    }

}
