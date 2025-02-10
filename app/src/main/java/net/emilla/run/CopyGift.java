package net.emilla.run;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import net.emilla.AssistActivity;

public class CopyGift implements Gift {

    private final AssistActivity mActivity;
    private final String mText;

    public CopyGift(AssistActivity act, String text) {
        mActivity = act;
        mText = text;
    }

    @Override
    public void run() {
        final var mgr = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        mgr.setPrimaryClip(ClipData.newPlainText(null, mText));
    }
}