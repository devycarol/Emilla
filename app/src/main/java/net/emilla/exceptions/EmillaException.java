package net.emilla.exceptions;

import androidx.annotation.NonNull;

public abstract class EmillaException extends RuntimeException {
    private final String mMessage;

    public EmillaException(@NonNull final String message) {
        super();
        mMessage = message;
    }

    @Override
    @NonNull
    public String getMessage() {
        return mMessage;
    }
}
