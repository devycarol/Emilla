package net.emilla.content;

import android.net.Uri;

import java.util.List;

public interface FileReceiver extends Receiver {

    /**
     * Provides attachments to the object. Be sure to trigger visual and spoken feedback as needed.
     *
     * @param attachments attachment set to give to the receiver.
     */
    void provide(List<Uri> attachments);
}
