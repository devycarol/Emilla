package net.emilla.run;

import android.content.ClipData;
import android.content.ClipboardManager;

import net.emilla.AssistActivity;
import net.emilla.util.Services;

public final class CopyGift implements Gift {

    private final AssistActivity mActivity;
    private final String mText;

    public CopyGift(AssistActivity act, String text) {
        mActivity = act;
        mText = text;
    }

    @Override
    public void run() {
        ClipboardManager clipMgr = Services.clipboard(mActivity);
        clipMgr.setPrimaryClip(ClipData.newPlainText(null, mText));
    }
}