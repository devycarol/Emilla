package net.emilla.content.receive;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.List;

public interface FileReceiver extends ResultReceiver {

    /**
     * Provides attachments to the object. Be sure to trigger visual and spoken feedback as needed.
     *
     * @param attachments attachment set to give to the receiver.
     */
    void provide(@NonNull List<Uri> attachments);
}
