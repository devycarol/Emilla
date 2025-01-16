package net.emilla.content.receive;

import androidx.annotation.NonNull;

public interface EmailReceiver extends ContactDataReceiver {

    /**
     * @param emailAddress is provided to the object.
     */
    void provide(@NonNull String emailAddress);
}
