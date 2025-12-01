package net.emilla.command;

import androidx.annotation.Nullable;

import net.emilla.activity.AssistActivity;

public abstract class CommandYielder {

    private EmillaCommand mCommand = null;

    protected CommandYielder() {}

    public abstract boolean usesInstruction();
    protected abstract EmillaCommand makeCommand(AssistActivity act);

    public final EmillaCommand command(AssistActivity act) {
        if (mCommand == null) {
            mCommand = makeCommand(act);
        }
        return mCommand;
    }

    public final EmillaCommand command(AssistActivity act, @Nullable String instruction) {
        EmillaCommand command = command(act);
        command.instruct(instruction);
        return command;
    }

}
