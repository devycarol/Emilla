package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.command.DataCmd;

public abstract class CoreDataCommand extends CoreCommand implements DataCmd {

    @Override
    public boolean usesData() {
        return true;
    }

    @Override
    public int imeAction() {
        return EditorInfo.IME_ACTION_NEXT;
    }

    protected CoreDataCommand(AssistActivity act, String instruct, @StringRes int name,
            @StringRes int instruction) {
        super(act, instruct, name, instruction);
    }

    /**
     * Executes the command with data. This should only be used externally!
     * @param data contents of command data field.
     */
    @Override
    public final void execute(@NonNull String data) {
        if (mInstruction == null) runWithData(data);
        else runWithData(mInstruction, data);
    }

    protected abstract void runWithData(String data);
    protected abstract void runWithData(String instruction, String data);
}
