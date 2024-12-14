package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

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

protected CoreDataCommand(AssistActivity act, String instruct, int nameId,
        int instructionId) {
    super(act, instruct, nameId, instructionId);
}

@Override
public void execute(String data) {
    if (mInstruction == null) runWithData(data);
    else runWithData(mInstruction, data);
}

protected abstract void runWithData(String data);
protected abstract void runWithData(String instruction, String data);
}
