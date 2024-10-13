package net.emilla.exceptions;

import androidx.annotation.NonNull;

public class EmlaFeatureException extends EmillaException {
    public EmlaFeatureException(@NonNull String message) { // TODO: handle at install
        super(message);
    }
}
