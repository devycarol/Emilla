package net.emilla.command;

import androidx.annotation.StringRes;

import net.emilla.Feedback;
import net.emilla.activity.AssistActivity;
import net.emilla.wadget.ActionSurface;

public interface DataCommand {
    @StringRes
    int dataHint();
    Feedback runWithData(ActionSurface surface, AssistActivity act, String data);
    Feedback runWithData(ActionSurface surface, AssistActivity act, String instruction, String data);

    default Feedback execute(ActionSurface surface, AssistActivity act, String data) {
        String instruction = ((EmillaCommand) this).instruction();
        return instruction != null
            ? runWithData(surface, act, instruction, data)
            : runWithData(surface, act, data)
        ;
    }
}
