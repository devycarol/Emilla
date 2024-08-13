package net.emilla.commands;

import android.view.inputmethod.EditorInfo;

import net.emilla.AssistActivity;

public abstract class CoreDataCommand extends CoreCommand implements DataCmd {
@Override
public boolean usesData() {
    return true;
}

@Override
public int imeAction() {
    return EditorInfo.IME_ACTION_NEXT;
}

protected CoreDataCommand(final AssistActivity act, final String instruct, final int nameId,
        final int instructionId) {
    super(act, instruct, nameId, instructionId);
}

@Override
public void execute(final String data) {
    if (mInstruction == null) runWithData(data);
    else runWithData(mInstruction, data);
}

protected abstract void runWithData(final String data);
protected abstract void runWithData(final String instruction, final String data);
}
