package net.emilla.content;

import android.net.Uri;

public interface ContactReceiver extends Receiver {

    /**
     * @param contact is provided to the object.
     */
    void provide(Uri contact);
}
