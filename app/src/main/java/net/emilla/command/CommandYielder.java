package net.emilla.command;

import androidx.annotation.Nullable;

import net.emilla.activity.AssistActivity;
import net.emilla.wadget.ActionSurface;

public abstract class CommandYielder {
    private EmillaCommand mCommand = null;

    protected CommandYielder() {
    }

    public abstract boolean usesInstruction();
    protected abstract EmillaCommand makeCommand(ActionSurface surface);

    public final EmillaCommand command(ActionSurface surface) {
        if (mCommand == null) {
            mCommand = makeCommand(surface);
        }
        return mCommand;
    }

    public final EmillaCommand command(
        AssistActivity act,
        @Nullable String instruction
    ) {
        EmillaCommand command = command(act);
        command.instruct(act, instruction);
        return command;
    }
}
