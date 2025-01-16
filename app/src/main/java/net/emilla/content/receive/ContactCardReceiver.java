package net.emilla.content.receive;

import android.net.Uri;

import androidx.annotation.NonNull;

public interface ContactCardReceiver extends ContactReceiver {

    /**
     * @param contact is provided to the object.
     */
    void provide(@NonNull Uri contact);
}
