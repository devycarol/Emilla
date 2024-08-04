package net.emilla.commands;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaBadCommandException;

public class CommandNotify extends CoreDataCommand {
public CommandNotify(final AssistActivity act) {
    super(act, R.string.command_notify, R.string.instruction_notify);
}

@Override @StringRes
public int dataHint() {
    return R.string.data_hint_notify;
}

@Override @DrawableRes
public int icon() {
    return R.drawable.ic_notify;
}

@Override
public void run() {
    throw new EmlaBadCommandException("Sorry! I don't know how to make reminders yet."); // Todo
}

@Override
public void run(final String text) {
    throw new EmlaBadCommandException("Sorry! I don't know how to make reminders yet."); // Todo
}

@Override
public void runWithData(final String data) {
    throw new EmlaBadCommandException("Sorry! I don't know how to make reminders yet."); // Todo
}

@Override
public void runWithData(final String instruction, final String data) {
    throw new EmlaBadCommandException("Sorry! I don't know how to make reminders yet."); // Todo
}
}
