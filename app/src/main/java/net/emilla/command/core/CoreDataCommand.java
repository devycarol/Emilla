package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.StringRes;

import net.emilla.activity.AssistActivity;
import net.emilla.command.DataCommand;

public abstract class CoreDataCommand extends CoreCommand implements DataCommand {

    @Override
    public final boolean usesData() {
        return true;
    }

    @StringRes
    private final int mHint;

    protected CoreDataCommand(AssistActivity act, CoreEntry coreEntry, @StringRes int dataHint) {
        super(act, coreEntry, EditorInfo.IME_ACTION_NEXT);
        mHint = dataHint;
    }

    @Override @StringRes
    public final int dataHint() {
        return mHint;
    }

    /// Executes the command with data. This should only be called externally!
    ///
    /// @param data contents of command data field.
    @Override
    public final void execute(String data) {
        String instruction = instruction();
        if (instruction != null) {
            runWithData(instruction, data);
        } else {
            runWithData(data);
        }
    }

    protected abstract void runWithData(String data);
    protected abstract void runWithData(String instruction, String data);
}
