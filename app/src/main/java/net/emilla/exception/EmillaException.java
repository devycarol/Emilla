package net.emilla.exception;

import androidx.annotation.StringRes;

import net.emilla.R;

public final class EmillaException extends RuntimeException {

    @StringRes
    public final int title;
    @StringRes
    public final int message;

    public EmillaException(@StringRes int message) {
        this(R.string.error, message);
    }

    public EmillaException(@StringRes int title, @StringRes int message) {
        super();

        this.title = title;
        this.message = message;
    }

}
