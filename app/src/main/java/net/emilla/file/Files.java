package net.emilla.file;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract.Document;

import androidx.annotation.Nullable;

import net.emilla.util.ArrayLoader;
import net.emilla.util.Intents;
import net.emilla.util.Strings;
import net.emilla.util.TokenIterator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class Files {

    private static final String WRITE = "w";
    private static final String WRITE_APPEND = "wa";

    private static final int INDEX_SIZE = 0;

    private static final String[] PROJECTION = {
        Document.COLUMN_SIZE
    };

    private static final long SIZE_UNKNOWN = -1L;

    @Nullable
    public static String[] nonBlankLines(ContentResolver cr, Uri file) {
        byte[] bytes;
        try (InputStream istream = cr.openInputStream(file)) {
            if (istream == null) {
                return null;
            }

            bytes = istream.readAllBytes();
        } catch (IOException e) {
            return null;
        }

        int lineCount = Strings.count(bytes, (byte) '\n') + 1;

        int last = bytes.length - 1;
        while (last >= 0 && bytes[last] == '\n') {
            --lineCount;
            --last;
        }

        var loader = new ArrayLoader<String>(lineCount, String[]::new);
        var tokens = new TokenIterator(bytes, (byte) '\n');

        for (int i = 0; i < lineCount; ++i) {
            String line = tokens.next();
            if (!line.isBlank()) {
                loader.add(line);
            }
        }

        return loader.array();
    }

    public static boolean writeLine(ContentResolver cr, Uri file, String text) {
        try (OutputStream ostream = cr.openOutputStream(file, WRITE)) {
            if (ostream == null) {
                throw new IOException("The content provider crashed");
            }

            writeLine(ostream, text);

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean appendLine(ContentResolver cr, Uri file, String text) {
        boolean endsNormally;
        try {
            endsNormally = isEmptyOrEndsWithLineSeparator(cr, file);
        } catch (IOException e) {
            return false;
        }

        try (OutputStream ostream = cr.openOutputStream(file, WRITE_APPEND)) {
            if (ostream == null) {
                return false;
            }

            if (!endsNormally) {
                ostream.write('\n');
            }

            writeLine(ostream, text);

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static void writeLine(OutputStream ostream, String text) throws IOException {
        byte[] bytes = text.getBytes();
        int last = bytes.length - 1;
        if (last >= 0) {
            ostream.write(bytes);
            if (bytes[last] == '\n') {
                return;
            }
        }

        ostream.write('\n');
    }

    private static long sizeOf(ContentResolver cr, Uri file)
        throws IOException {

        try (Cursor cursor = cr.query(file, PROJECTION, null, null, null)) {
            if (cursor == null || !cursor.moveToFirst()) {
                throw new IOException("Failed to query file size");
            }

            if (cursor.isNull(INDEX_SIZE)) {
                return SIZE_UNKNOWN;
            }

            long size = cursor.getLong(INDEX_SIZE);
            if (size < 0L) {
                throw new IOException("The content provider reported a negative file size");
            }

            return size;
        }
    }

    private static boolean isEmptyOrEndsWithLineSeparator(ContentResolver cr, Uri file)
        throws IOException, FileNotFoundException {

        long fileSize = sizeOf(cr, file);

        try (InputStream istream = cr.openInputStream(file)) {
            if (istream == null) {
                throw new IOException("The content provider crashed");
            }

            if (fileSize == 0L) {
                assertEnded(istream);
                return true;
            }

            return isLineSeparator(lastByteOf(istream, fileSize));
        }
    }

    private static boolean isLineSeparator(int b) {
        return b == '\n' || b == '\r';
    }

    private static int lastByteOf(InputStream istream, long size) throws IOException {
        int lastByte;
        if (size == SIZE_UNKNOWN) {
            lastByte = -1;
            do {
                int b = istream.read();
                if (b == -1) {
                    break;
                }

                lastByte = b;
            } while (true);

        } else {
            istream.skipNBytes(size - 1L);

            lastByte = istream.read();

            assertEnded(istream);
        }

        if (lastByte == -1) {
            throw wrongFileSize();
        }

        return lastByte;
    }

    private static void assertEnded(InputStream istream) throws IOException {
        if (istream.read() != -1) {
            throw wrongFileSize();
        }
    }

    private static IOException wrongFileSize() {
        return new IOException("The file size was not correctly reported");
    }

    public static Intent viewIntent(Uri file, String mimeType) {
        return Intents.view(file, mimeType)
            .putExtra(Intent.EXTRA_STREAM, file)
            .addFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
    }

    private Files() {}

}
