package net.emilla.command;

import androidx.annotation.StringRes;

import net.emilla.activity.AssistActivity;

public interface DataCommand {

    @StringRes
    int dataHint();
    void runWithData(AssistActivity act, String data);
    void runWithData(AssistActivity act, String instruction, String data);

    static void execute(DataCommand cmd, AssistActivity act, String data) {
        String instruction = ((EmillaCommand) cmd).instruction();
        if (instruction != null) {
            cmd.runWithData(act, instruction, data);
        } else {
            cmd.runWithData(act, data);
        }
    }

}
