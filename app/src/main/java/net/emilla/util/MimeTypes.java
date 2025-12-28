package net.emilla.util;

import android.webkit.MimeTypeMap;

public enum MimeTypes {
    ;

    public static final String PREFIX_TEXT = "text/";

    public static final String ANY_TEXT = PREFIX_TEXT + '*';
    public static final String PLAIN_TEXT = PREFIX_TEXT + "plain";
    public static final String CALENDAR_EVENT = "vnd.android.cursor.dir/event";

    private static final String EXTENSION_TEXT = ".txt";

    /// Converts the string to a ".txt" filename if its type isn't a known text format.
    public static String textFilename(String filename) {
        int dotIndex = filename.lastIndexOf('.');

        if (dotIndex < 0) {
            return filename + EXTENSION_TEXT;
        }

        var typeMap = MimeTypeMap.getSingleton();
        String extension = filename.substring(dotIndex + 1);
        String mimeType = typeMap.getMimeTypeFromExtension(extension);

        return mimeType != null && mimeType.startsWith(PREFIX_TEXT)
            ? filename
            : filename + EXTENSION_TEXT;
    }

}
