package net.emilla.command.core;

import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.command.EmillaCommand;
import net.emilla.utils.Lang;

public abstract class CoreCommand extends EmillaCommand {
@StringRes private final int
    mNameId,
    mInstructionId;

public CoreCommand(AssistActivity act, String instruct, @StringRes int nameId,
        @StringRes int instructionId) {
    super(act, instruct);

    mNameId = nameId;
    mInstructionId = instructionId;
}

@Override
protected CharSequence name() {
    return string(mNameId);
}

@Override
protected CharSequence dupeLabel() {
    return string(mNameId) + " (Emilla command)";
}

@Override
public CharSequence lcName() {
    return string(mNameId).toLowerCase();
}

@Override
public CharSequence title() {
    return Lang.colonConcat(resources(), mNameId, mInstructionId);
}
}
