package net.emilla.command;

import androidx.annotation.StringRes;

import net.emilla.activity.AssistActivity;

public interface DataCommand {
    @StringRes
    int dataHint();
    void runWithData(AssistActivity act, String data);
    void runWithData(AssistActivity act, String instruction, String data);

    default void execute(AssistActivity act, String data) {
        String instruction = ((EmillaCommand) this).instruction();
        if (instruction != null) {
            runWithData(act, instruction, data);
        } else {
            runWithData(act, data);
        }
    }
}
