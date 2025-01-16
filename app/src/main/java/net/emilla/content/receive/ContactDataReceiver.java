package net.emilla.content.receive;

import androidx.annotation.NonNull;

public interface ContactDataReceiver extends ContactReceiver {

    /**
     * @param data is provided to the object.
     */
    void provide(@NonNull String data);
}
