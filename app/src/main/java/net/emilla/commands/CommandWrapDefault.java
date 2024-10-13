package net.emilla.commands;

import androidx.annotation.DrawableRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Lang;

public class CommandWrapDefault extends EmillaCommand {
private final EmillaCommand mCmd; // Todo: allow app commands

@Override
protected CharSequence name() {
    return mCmd.name();
}

@Override
protected CharSequence dupeLabel() {
    // Todo: exclude this from the interface for wrappers
    return null;
}

@Override
public CharSequence lcName() {
    // Todo: exclude this from the interface for wrappers
    return null;
}

@Override
public CharSequence title() {
    return Lang.colonConcat(resources(), R.string.command_default, mCmd.lcName());
}

@Override @DrawableRes
public int icon() {
    return mCmd.icon();
}

@Override
public int imeAction() {
    return mCmd.imeAction();
}

protected CommandWrapDefault(AssistActivity act, String instruct,
        EmillaCommand cmd) {
    super(act, instruct);
    mCmd = cmd;
}

@Override
protected void run() {
    mCmd.run();
}

@Override
protected void run(String instruction) {
    mCmd.run(instruction);
}
}
