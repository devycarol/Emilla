package net.emilla.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public enum Clipboard {;
    public static void copy(Context ctx, CharSequence text) {
        ClipboardManager clipboard = Services.clipboard(ctx);
        clipboard.setPrimaryClip(ClipData.newPlainText(null, text));
    }
}
