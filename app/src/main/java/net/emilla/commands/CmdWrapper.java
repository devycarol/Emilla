package net.emilla.commands;

import net.emilla.AssistActivity;

public abstract class CmdWrapper extends EmillaCommand {
protected CmdWrapper(final AssistActivity act) {
    super(act);
}
}
