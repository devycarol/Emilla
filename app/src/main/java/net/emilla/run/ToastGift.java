package net.emilla.run;

import net.emilla.activity.AssistActivity;

public final class ToastGift implements Runnable {

    private final AssistActivity mActivity;
    private final CharSequence mMessage;
    private final boolean mLong;

    public ToastGift(AssistActivity activity, CharSequence msg, boolean longToast) {
        mActivity = activity;
        mMessage = msg;
        mLong = longToast;
    }

    @Override
    public void run() {
        mActivity.toast(mMessage, mLong);
    }
}
