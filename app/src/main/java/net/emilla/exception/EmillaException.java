package net.emilla.exception;

import androidx.annotation.NonNull;

public abstract class EmillaException extends RuntimeException {

    private final String mMessage;

    public EmillaException(@NonNull String message) {
        super();
        mMessage = message;
    }

    @Override @NonNull
    public String getMessage() {
        return mMessage;
    }
}
