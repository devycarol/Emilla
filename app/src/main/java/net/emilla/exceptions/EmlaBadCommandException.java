package net.emilla.exceptions;

import androidx.annotation.NonNull;

public class EmlaBadCommandException extends EmillaException {
    public EmlaBadCommandException(@NonNull final String message) {
        super(message);
    }
}
