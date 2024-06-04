package net.emilla.exceptions;

import androidx.annotation.NonNull;

public class EmlaPermissionsException extends EmillaException {
    public EmlaPermissionsException(@NonNull final String message) {
        super(message);
    }
}
