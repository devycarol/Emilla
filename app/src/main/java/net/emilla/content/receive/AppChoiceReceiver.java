package net.emilla.content.receive;

public interface AppChoiceReceiver extends ResultReceiver {

    /**
     * Notifies the object of whether the chooser was accepted or dismissed
     *
     * @param chosen true if an app was chosen, false if the chooser was dismissed.
     */
    void provide(boolean chosen);
}
