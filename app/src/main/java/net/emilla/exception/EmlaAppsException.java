package net.emilla.exception;

import androidx.annotation.StringRes;

public final class EmlaAppsException extends EmillaException {

    public EmlaAppsException(@StringRes int title, @StringRes int msg) {
        super(title, msg);
    }
}
