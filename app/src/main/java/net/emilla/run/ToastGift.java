package net.emilla.run;

import net.emilla.activity.AssistActivity;
import net.emilla.util.Toasts;

public final class ToastGift implements CommandRun {

    private final CharSequence mMessage;
    private final boolean mIsLongToast;

    public ToastGift(CharSequence msg, boolean isLongToast) {
        mMessage = msg;
        mIsLongToast = isLongToast;
    }

    @Override
    public void run(AssistActivity act) {
        Toasts.show(act, mMessage, mIsLongToast);
    }

}
