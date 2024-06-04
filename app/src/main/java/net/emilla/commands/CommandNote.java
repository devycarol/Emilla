package net.emilla.commands;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;

public class CommandNote extends CoreCommand implements DataCommand {
public CommandNote(final AssistActivity act) {
    super(act, R.string.command_note, R.string.instruction_file);
}

@Override
public Command cmd() {
    return Command.NOTE;
}

@Override
public void run() {
    throw new EmlaAppsException("Sorry! I don't know how to write notes yet.");
}

@Override
public void run(final String title) {
    throw new EmlaAppsException("Sorry! I don't know how to write notes yet.");
}

@Override
public void runWithData(final String text) {
    throw new EmlaAppsException("Sorry! I don't know how to write notes yet.");
}

@Override
public void runWithData(final String title, final String text) {
    throw new EmlaAppsException("Sorry! I don't know how to write notes yet.");
}
}
