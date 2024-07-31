package net.emilla.commands;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaBadCommandException;

public class CommandNotify extends CoreCommand {
public CommandNotify(final AssistActivity act) {
    super(act, R.string.command_notify, R.string.instruction_notify);
}

@Override
public Command cmd() {
    return Command.NOTIFY;
}

@Override
public void run() {
    throw new EmlaBadCommandException("Sorry! I don't know how to make reminders yet."); // TODO
}

@Override
public void run(final String text) {
    throw new EmlaBadCommandException("Sorry! I don't know how to make reminders yet."); // TODO
}
}
