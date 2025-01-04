package net.emilla.exception;

import androidx.annotation.StringRes;

public class EmlaFeatureException extends EmillaException {

    @Deprecated
    public EmlaFeatureException(@StringRes int title, @StringRes int msg) {
        // TODO: handle these at install
        super(title, msg);
    }
}
