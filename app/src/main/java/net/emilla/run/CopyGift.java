package net.emilla.run;

import android.content.ClipData;
import android.content.ClipboardManager;

import androidx.annotation.StringRes;

import net.emilla.activity.AssistActivity;
import net.emilla.util.Services;

public final class CopyGift implements Gift {

    private final AssistActivity mActivity;
    private final CharSequence mText;

    public CopyGift(AssistActivity act, @StringRes int text) {
        this(act, act.getString(text));
    }

    public CopyGift(AssistActivity act, CharSequence text) {
        mActivity = act;
        mText = text;
    }

    @Override
    public void run() {
        ClipboardManager clipMgr = Services.clipboard(mActivity);
        clipMgr.setPrimaryClip(ClipData.newPlainText(null, mText));
    }
}