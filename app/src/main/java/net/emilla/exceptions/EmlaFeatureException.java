package net.emilla.exceptions;

import androidx.annotation.NonNull;

public class EmlaFeatureException extends EmillaException {
    public EmlaFeatureException(@NonNull final String message) { // TODO: handle at install
        super(message);
    }
}
