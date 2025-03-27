package net.emilla.exception;

import androidx.annotation.StringRes;

public final class EmillaException extends RuntimeException {

    @StringRes
    public final int title, message;

    public EmillaException(@StringRes int title, @StringRes int message) {
        super();

        this.title = title;
        this.message = message;
    }
}
