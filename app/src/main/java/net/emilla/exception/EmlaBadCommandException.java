package net.emilla.exception;

import androidx.annotation.StringRes;

public class EmlaBadCommandException extends EmillaException {

    public EmlaBadCommandException(@StringRes int title, @StringRes int msg) {
        super(title, msg);
    }
}
