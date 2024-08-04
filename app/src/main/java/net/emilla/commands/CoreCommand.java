package net.emilla.commands;

import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.utils.Lang;

public abstract class CoreCommand extends EmillaCommand {
@StringRes
private final int
    mNameId,
    mInstructionId;

public CoreCommand(final AssistActivity act, @StringRes final int nameId,
        @StringRes final int instructionId) {
    super(act);

    mNameId = nameId;
    mInstructionId = instructionId;
}

@Override
protected CharSequence name() {
    return resources().getString(mNameId);
}

@Override
protected CharSequence dupeLabel() {
    return resources().getString(mNameId) + " (Emilla command)";
}

@Override
public CharSequence lcName() {
    return resources().getString(mNameId).toLowerCase();
}

@Override
public CharSequence title() {
    return Lang.colonConcat(resources(), mNameId, mInstructionId);
}
}
