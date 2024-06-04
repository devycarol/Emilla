package net.emilla.commands;

public interface DataCommand {
    /**
     * @param data is provided from the data field. Unlike `instruction`, this shouldn't be
     *             considered trim-safe.
     */
    void runWithData(String data);
    /**
     * @param instruction is provided after in the command field after the command's name. It's
     *                    always space-trimmed should remain as such.
     * @param data is provided from the data field. Unlike `instruction`, this shouldn't be
     *             considered trim-safe.
     */
    void runWithData(String instruction, String data);
}
