package net.emilla.commands;

import android.app.AlertDialog;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Dialogs;

public class DuplicateCommand extends EmillaCommand implements DataCommand {
public static DuplicateCommand instance(final AssistActivity act, final EmillaCommand cmd1,
        final EmillaCommand cmd2) {
    final boolean dupe1 = cmd1 instanceof DuplicateCommand;
    final boolean dupe2 = cmd2 instanceof DuplicateCommand;
    if (!(dupe1 || dupe2)) return new DuplicateCommand(act, cmd1, cmd2);
    if (dupe1 && !dupe2) return new DuplicateCommand(act, (DuplicateCommand) cmd1, cmd2);
    if (!dupe1) return new DuplicateCommand(act, cmd1, (DuplicateCommand) cmd2);
    return new DuplicateCommand(act, (DuplicateCommand) cmd1, (DuplicateCommand) cmd2);
}

@Override
public Command cmd() {
    return Command.DUPLICATE;
}

@Override
protected CharSequence name() {
    return resources().getString(R.string.command_duplicate);
}

@Override
protected CharSequence dupeLabel() {
    return "You shouldn't see this \uD83D\uDE43";
}

@Override
public CharSequence lcName() {
    return resources().getString(R.string.command_duplicate).toLowerCase();
}

@Override
public CharSequence title() {
    return resources().getString(R.string.command_duplicate);
}

@Override @ArrayRes
public int detailsId() {
    return R.array.details_duplicate;
}

@Override @StringRes
public int dataHint() {
    return R.string.data_hint_default;
}

@Override @DrawableRes
public int icon() {
    return R.drawable.ic_command;
}

@Override
public int imeAction() {
    return EditorInfo.IME_ACTION_NEXT;
}

private final CharSequence[] mLabels;
private final EmillaCommand[] mCommands;
private final AlertDialog.Builder mBuilder;

private DuplicateCommand(final AssistActivity act, final int cmdCount) {
    super(act);

    mCommands = new EmillaCommand[cmdCount];
    mLabels = new CharSequence[cmdCount];
    mBuilder = Dialogs.base(act, R.string.dialog_command);
}

private DuplicateCommand(final AssistActivity act, final EmillaCommand cmd1,
        final EmillaCommand cmd2) {
    this(act, 2);

    mCommands[0] = cmd1;
    mLabels[0] = cmd1.dupeLabel();
    mCommands[1] = cmd2;
    mLabels[1] = cmd2.dupeLabel();
}

private DuplicateCommand(final AssistActivity act, final DuplicateCommand cmd1,
        final EmillaCommand cmd2) {
    this(act, cmd1.mCommands.length + 1);

    int idx = -1;
    for (final EmillaCommand dup : cmd1.mCommands) {
        mCommands[++idx] = dup;
        mLabels[idx] = dup.dupeLabel();
    }
    mCommands[++idx] = cmd2;
    mLabels[idx] = cmd2.dupeLabel();
}

private DuplicateCommand(final AssistActivity act, final EmillaCommand cmd1,
        final DuplicateCommand cmd2) {
    this(act, 1 + cmd2.mCommands.length);

    mCommands[0] = cmd1;
    mLabels[0] = cmd1.dupeLabel();
    int idx = 0;
    for (final EmillaCommand dup : cmd2.mCommands) {
        mCommands[++idx] = dup;
        mLabels[idx] = dup.dupeLabel();
    }
}

private DuplicateCommand(final AssistActivity act, final DuplicateCommand cmd1,
        final DuplicateCommand cmd2) {
    this(act, cmd1.mCommands.length + cmd2.mCommands.length);

    int idx = -1;
    for (final EmillaCommand dup : cmd1.mCommands) {
        mCommands[++idx] = dup;
        mLabels[idx] = dup.dupeLabel();
    }
    for (final EmillaCommand dup : cmd2.mCommands) {
        mCommands[++idx] = dup;
        mLabels[idx] = dup.dupeLabel();
    }
}

private void chooseCommand(final AlertDialog.Builder builder) {
    offer(builder.create());
}

@Override
public void run() {
    // TODO: don't do base app commands past their initial name
    mBuilder.setItems(mLabels, (dialog, which) -> mCommands[which].run());
    chooseCommand(mBuilder);
}

@Override
public void run(final String instruction) {
    mBuilder.setItems(mLabels, (dialog, which) -> mCommands[which].run(instruction));
    chooseCommand(mBuilder);
}

@Override
public void runWithData(final String data) {
    mBuilder.setItems(mLabels, (dialog, which) -> {
        final EmillaCommand cmd = mCommands[which];
        if (cmd instanceof DataCommand) ((DataCommand) cmd).runWithData(data);
        else cmd.run(data); // TODO: handle this more gracefully
    });
    chooseCommand(mBuilder);
}

@Override
public void runWithData(final String instruction, final String data) {
    mBuilder.setItems(mLabels, (dialog, which) -> {
        final EmillaCommand cmd = mCommands[which];
        if (cmd instanceof DataCommand) ((DataCommand) cmd).runWithData(instruction, data);
        else cmd.run(instruction + '\n' + data); // TODO: handle this more gracefully
    });
    chooseCommand(mBuilder);
}
}
