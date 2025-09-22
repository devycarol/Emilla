package net.emilla.run;

import net.emilla.activity.AssistActivity;

public final class ToastGift implements CommandRun {

    private final CharSequence mMessage;
    private final boolean mLong;

    public ToastGift(CharSequence msg, boolean longToast) {
        mMessage = msg;
        mLong = longToast;
    }

    @Override
    public void run(AssistActivity act) {
        act.toast(mMessage, mLong);
    }
}
