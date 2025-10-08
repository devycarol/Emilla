package net.emilla.content.receive;

public interface ContactDataReceiver extends ContactReceiver {

    /// Provides the receiver with contact data.
    ///
    /// @param data is provided to the receiver.
    void provide(String data);
}
