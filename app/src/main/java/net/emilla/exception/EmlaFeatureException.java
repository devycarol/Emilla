package net.emilla.exception;

import androidx.annotation.StringRes;

public final class EmlaFeatureException extends EmillaException {

    @Deprecated
    public EmlaFeatureException(@StringRes int title, @StringRes int msg) {
        // TODO: handle these at install
        super(title, msg);
    }
}
