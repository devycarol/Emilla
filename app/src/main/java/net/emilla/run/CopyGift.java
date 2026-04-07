package net.emilla.run;

import android.content.ClipData;
import android.content.ClipboardManager;

import net.emilla.util.Services;

public enum CopyGift {;
    public static CommandRun instance(CharSequence text) {
        return act -> {
            ClipboardManager clipboard = Services.clipboard(act);
            clipboard.setPrimaryClip(ClipData.newPlainText(null, text));
        };
    }
}
