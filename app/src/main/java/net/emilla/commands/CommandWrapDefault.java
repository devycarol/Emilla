package net.emilla.commands;

import androidx.annotation.DrawableRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Lang;

public class CommandWrapDefault extends CmdWrapper {
private final CoreCommand mCmd; // Todo: allow app commands

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

protected CommandWrapDefault(final AssistActivity act, final CoreCommand cmd) {
    super(act);
    mCmd = cmd;
}

@Override
public void run() {
    mCmd.run();
}

@Override
public void run(final String instruction) {
    mCmd.run(instruction);
}
}
