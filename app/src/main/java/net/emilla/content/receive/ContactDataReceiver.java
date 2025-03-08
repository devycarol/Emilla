package net.emilla.content.receive;

public interface ContactDataReceiver extends ContactReceiver {

    /**
     * @param data is provided to the object.
     */
    void provide(String data);
}
