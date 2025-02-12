package net.emilla.exception;

import androidx.annotation.StringRes;

public final class EmlaBadCommandException extends EmillaException {

    public EmlaBadCommandException(@StringRes int title, @StringRes int msg) {
        super(title, msg);
    }
}
