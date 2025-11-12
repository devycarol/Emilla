package net.emilla.content.receive;

import net.emilla.activity.AssistActivity;

public interface ContactDataReceiver extends ContactReceiver {

    /// Provides the receiver with contact data.
    ///
    /// @param data is provided to the receiver.
    void provide(AssistActivity act, String data);

}
