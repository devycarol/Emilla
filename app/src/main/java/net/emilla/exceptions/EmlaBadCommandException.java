package net.emilla.exceptions;

import androidx.annotation.NonNull;

public class EmlaBadCommandException extends EmillaException {
    public EmlaBadCommandException(@NonNull String message) {
        super(message);
    }
}
