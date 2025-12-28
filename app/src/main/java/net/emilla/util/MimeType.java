package net.emilla.util;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.util.Iterator;
import java.util.regex.Pattern;

public enum MimeType {
    ;

    private static final Pattern SLASH = Pattern.compile("/");

    /// Returns the union of all MIME types in a sequence of files. The union of different
    /// type-parts is the wildcard, so this will broaden the scope of the MIME type beyond the
    /// simple union.
    ///
    /// @param fileUris files to get MIME types from.
    /// @return the union of all provided MIME types.
    @Deprecated
    public static String of(Iterable<Uri> fileUris, Context ctx) {
        Iterator<Uri> itr = fileUris.iterator();
        String type = of(itr.next(), ctx);
        while (!type.equals("*/*") && itr.hasNext()) {
            type = union(type, of(itr.next(), ctx));
        }
        return type;
    }

    /// Returns the union of all MIME types in a sequence of files. The union of different
    /// type-parts is the wildcard, so this will broaden the scope of the MIME type beyond the
    /// simple union.
    ///
    /// @param type base type to start with.
    /// @param fileUris files to get MIME types from.
    /// @return the union of all provided MIME types.
    @Deprecated
    public static String of(String type, Iterable<? extends Uri> fileUris, Context ctx) {
        for (Uri fileUri : fileUris) {
            type = union(type, of(fileUri, ctx));
            if (type.equals("*/*")) return "*/*";
        }
        return type;
    }

    /// Returns the MIME type of a file from its URI.
    ///
    /// @param fileUri file to get MIME type from
    /// @return the MIME type of the given file.
    public static String of(Uri fileUri, Context ctx) {
        if (ContentResolver.SCHEME_CONTENT.equals(fileUri.getScheme())) {
            return ctx.getContentResolver().getType(fileUri);
        }
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(fileUri.toString());
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
    }

    @Deprecated
    private static String union(CharSequence type1, CharSequence type2) {
        String[] parts1 = SLASH.split(type1);
        String[] parts2 = SLASH.split(type2);
        return partUnion(parts1[0], parts2[0]) + '/' + partUnion(parts1[1], parts2[1]);
    }

    @Deprecated
    private static String partUnion(String part1, String part2) {
        return part1.equals(part2) ? part1 : "*";
    }

}
