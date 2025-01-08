package net.emilla.command.core;

import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.command.EmillaCommand;
import net.emilla.lang.Lang;
import net.emilla.run.MessageFailure;

public abstract class CoreCommand extends EmillaCommand {

    @StringRes private final int
            mName,
            mInstruction;

    public CoreCommand(AssistActivity act, String instruct, @StringRes int name,
            @StringRes int instruction) {
        super(act, instruct);

        mName = name;
        mInstruction = instruction;
    }

    @Override
    public CharSequence name() {
        return string(mName);
    }

    @Override
    protected CharSequence dupeLabel() {
        return string(mName) + " (Emilla command)";
    }

    @Override
    public CharSequence lcName() {
        return string(mName).toLowerCase();
    }

    @Override
    public CharSequence title() {
        return Lang.colonConcat(resources, mName, mInstruction);
    }

    protected void fail(@StringRes int msg) {
        fail(new MessageFailure(activity, mName, msg));
    }
}
