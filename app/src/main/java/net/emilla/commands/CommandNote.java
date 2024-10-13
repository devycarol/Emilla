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

public CommandNote(AssistActivity act, String instruct) {
    super(act, instruct, R.string.command_note, R.string.instruction_file);
}

@Override @StringRes
public int dataHint() {
    return R.string.data_hint_note;
}

@Override
protected void run() {
    throw new EmlaAppsException("Sorry! I don't know how to write notes yet.");
}

@Override
protected void run(String title) {
    throw new EmlaAppsException("Sorry! I don't know how to write notes yet.");
}

@Override
protected void runWithData(String text) {
    throw new EmlaAppsException("Sorry! I don't know how to write notes yet.");
}

@Override
protected void runWithData(String title, String text) {
    throw new EmlaAppsException("Sorry! I don't know how to write notes yet.");
}
}
