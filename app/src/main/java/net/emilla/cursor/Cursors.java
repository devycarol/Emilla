package net.emilla.cursor;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.Nullable;

import net.emilla.util.ArrayLoader;

public enum Cursors {
    ;

    public static boolean testFirst(ContentResolver cr, Uri uri, CursorTester tester) {
        try (Cursor cursor = cr.query(uri, tester.mProjection, null, null, null)) {
            return cursor != null
                && cursor.moveToFirst()
                && tester.test(cursor);
        } catch (SecurityException e) {
            return false;
        }
    }

    @Nullable
    public static <T> T extractFirst(ContentResolver cr, Uri uri, CursorExtractor<T> extractor) {
        try (Cursor cursor = cr.query(uri, extractor.mProjection, null, null, null)) {
            if (cursor == null || !cursor.moveToFirst()) {
                return null;
            }
            return extractor.extract(cursor);
        } catch (SecurityException e) {
            return null;
        }
    }

    @Nullable
    public static <T> T[] items(ContentResolver cr, Uri uri, CursorArrayExtractor<T> extractor) {
        try (Cursor cursor = cr.query(uri, extractor.mProjection, null, null, null)) {
            if (cursor == null) {
                return null;
            }

            int count = cursor.getCount();
            var loader = new ArrayLoader<T>(count, extractor::newArray);

            while (cursor.moveToNext()) {
                if (extractor.filter(cursor)) {
                    loader.growingAdd(extractor.extract(cursor));
                }
            }

            return loader.array();
        } catch (SecurityException e) {
            return null;
        }
    }

}
