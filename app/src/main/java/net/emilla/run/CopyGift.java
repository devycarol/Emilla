package net.emilla.run;

import android.content.ClipData;
import android.content.ClipboardManager;

import net.emilla.activity.AssistActivity;
import net.emilla.util.Services;

public final class CopyGift implements CommandRun {

    private final CharSequence mText;

    public CopyGift(CharSequence text) {
        mText = text;
    }

    @Override
    public void run(AssistActivity act) {
        ClipboardManager clipMgr = Services.clipboard(act);
        clipMgr.setPrimaryClip(ClipData.newPlainText(null, mText));
    }
}