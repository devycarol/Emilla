package net.emilla.content.receive;

import androidx.annotation.NonNull;

public interface PhoneReceiver extends ContactDataReceiver {

    /**
     * @param phoneNumber is provided to the object.
     */
    void provide(@NonNull String phoneNumber);
}
