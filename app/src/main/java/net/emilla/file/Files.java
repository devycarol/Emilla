package net.emilla.file;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract.Document;

import androidx.annotation.Nullable;

import net.emilla.util.Chars;
import net.emilla.util.Intents;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class Files {

    private static final String OVERWRITE = "wt";
    private static final String APPEND = "wa";

    private static final int INDEX_SIZE = 0;

    private static final String[] PROJECTION = {
        Document.COLUMN_SIZE
    };

    private static final long SIZE_UNKNOWN = -1L;

    @FunctionalInterface
    private interface Reader<T> {
        T readFrom(InputStream istream) throws IOException;
    }

    private static <T> T read(ContentResolver cr, Uri file, Reader<T> reader)
        throws IOException, FileNotFoundException {

        try (InputStream istream = cr.openInputStream(file)) {
            requireStream(istream);

            return reader.readFrom(istream);
        } catch (SecurityException e) {
            throw new IOException(e);
        }
    }

    @FunctionalInterface
    private interface Writer {
        void writeTo(OutputStream ostream) throws IOException;
    }

    private static void write(ContentResolver cr, Uri file, String mode, Writer writer)
        throws IOException, FileNotFoundException {

        try (OutputStream ostream = cr.openOutputStream(file, mode)) {
            requireStream(ostream);

            writer.writeTo(ostream);
        } catch (SecurityException e) {
            throw new IOException(e);
        }
    }

    @Nullable
    public static ListItem[] textList(ContentResolver cr, Uri file) {
        byte[] bytes;
        try {
            bytes = read(cr, file, Files::readAllBytes);
        } catch (IOException e) {
            return null;
        }

        return new TextListReader(bytes).toArray();
    }

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return inputStream.readAllBytes();
        }

        var ostream = new ByteArrayOutputStream();

        int bufferSize = 0x2000;
        var buffer = new byte[bufferSize];
        do {
            int read = inputStream.read(buffer, 0, bufferSize);
            if (read == -1) {
                break;
            }
            ostream.write(buffer, 0, read);
        } while (true);

        return ostream.toByteArray();
    }

    public static boolean saveList(ContentResolver cr, ListItem[] list, Uri file) {
        try {
            write(cr, file, OVERWRITE, ostream -> {
                for (ListItem item : list) {
                    item.writeTo(ostream);
                    ostream.write('\n');
                }
            });

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean writeLine(ContentResolver cr, Uri file, String text) {
        try {
            write(cr, file, OVERWRITE, ostream -> writeLine(ostream, text));

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

        try {
            write(cr, file, APPEND, ostream -> {
                if (!endsNormally) {
                    ostream.write('\n');
                }

                writeLine(ostream, text);
            });

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
            requireStream(istream);

            if (fileSize == 0L) {
                assertEnded(istream);
                return true;
            }

            return Chars.isLineSeparator(lastByteOf(istream, fileSize));
        } catch (SecurityException e) {
            throw new IOException(e);
        }
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

    private static void requireStream(Object iostream) throws IOException {
        if (iostream == null) {
            throw new IOException("The content provider crashed");
        }
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
