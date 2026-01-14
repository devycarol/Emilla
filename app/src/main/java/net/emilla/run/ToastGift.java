package net.emilla.run;

import net.emilla.util.Toasts;

public enum ToastGift {
    ;
    public static CommandRun instance(CharSequence message, boolean isLongToast) {
        return act -> Toasts.show(act, message, isLongToast);
    }
}
