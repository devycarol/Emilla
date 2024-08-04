package net.emilla.commands;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;

public class CommandNote extends CoreDataCommand {
@Override @ArrayRes
public int detailsId() {
    return R.array.details_note;
}

@Override @DrawableRes
public int icon() {
    return R.drawable.ic_note;
}

public CommandNote(final AssistActivity act) {
    super(act, R.string.command_note, R.string.instruction_file);
}

@Override @StringRes
public int dataHint() {
    return R.string.data_hint_note;
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
