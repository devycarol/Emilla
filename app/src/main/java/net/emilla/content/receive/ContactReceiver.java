package net.emilla.content.receive;

import android.net.Uri;

public interface ContactReceiver extends ResultReceiver {

    /**
     * @param contact is provided to the object.
     */
    void provide(Uri contact);
}
