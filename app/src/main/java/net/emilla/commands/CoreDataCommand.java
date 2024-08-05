package net.emilla.commands;

import android.view.inputmethod.EditorInfo;

import net.emilla.AssistActivity;

public abstract class CoreDataCommand extends CoreCommand implements DataCommand {
@Override
public boolean usesData() {
    return true;
}

@Override
public int imeAction() {
    return EditorInfo.IME_ACTION_NEXT;
}

protected CoreDataCommand(final AssistActivity act, final int nameId, final int instructionId) {
    super(act, nameId, instructionId);
}
}
