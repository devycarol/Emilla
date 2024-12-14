package net.emilla.command.core;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaBadCommandException;

public class Notify extends CoreDataCommand {
public Notify(AssistActivity act, String instruct) {
    super(act, instruct, R.string.command_notify, R.string.instruction_notify);
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
protected void run() {
    throw new EmlaBadCommandException("Sorry! I don't know how to make reminders yet."); // Todo
}

@Override
protected void run(String text) {
    throw new EmlaBadCommandException("Sorry! I don't know how to make reminders yet."); // Todo
}

@Override
protected void runWithData(String data) {
    throw new EmlaBadCommandException("Sorry! I don't know how to make reminders yet."); // Todo
}

@Override
protected void runWithData(String instruction, String data) {
    throw new EmlaBadCommandException("Sorry! I don't know how to make reminders yet."); // Todo
}
}
