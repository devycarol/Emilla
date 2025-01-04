package net.emilla.exception;

import androidx.annotation.StringRes;

public abstract class EmillaException extends RuntimeException {

    @StringRes
    private final int mTitle, mMessage;

    public EmillaException(@StringRes int title, @StringRes int msg) {
        super();
        mTitle = title;
        mMessage = msg;
    }

    @StringRes
    public int title() {
        return mTitle;
    }

    @StringRes
    public int message() {
        return mMessage;
    }
}
