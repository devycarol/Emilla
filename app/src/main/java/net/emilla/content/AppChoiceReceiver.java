package net.emilla.content;

public interface AppChoiceReceiver extends Receiver {

    /**
     * Notifies the object of whether the chooser was accepted or dismissed
     *
     * @param chosen true if an app was chosen, false if the chooser was dismissed.
     */
    void provide(boolean chosen);
}
