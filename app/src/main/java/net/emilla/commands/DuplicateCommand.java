package net.emilla.commands;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Dialogs;

public class DuplicateCommand extends EmillaCommand implements DataCmd {
@Override
protected CharSequence name() {
    return string(R.string.command_duplicate);
}

@Override
protected CharSequence dupeLabel() {
    return "You shouldn't see this \uD83D\uDE43";
}

@Override
public CharSequence lcName() {
    return string(R.string.command_duplicate).toLowerCase();
}

@Override
public CharSequence title() {
    return string(R.string.command_duplicate);
}

@Override @ArrayRes
public int detailsId() {
    return R.array.details_duplicate;
}

@Override @StringRes
public int dataHint() {
    return R.string.data_hint_default;
}

@Override
public boolean usesData() {
    return mUsesData;
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
private final boolean mUsesData;

public DuplicateCommand(final AssistActivity act, final String instruct, final EmillaCommand[] cmds) {
    super(act, instruct);

    mCommands = cmds;
    mLabels = new CharSequence[cmds.length];
    boolean usesData = false;
    int i = -1;
    for (final EmillaCommand cmd : cmds) {
        if (!usesData && cmd.usesData()) usesData = true;
        mLabels[++i] = cmd.dupeLabel();
        // Todo: whittle down when data is entered and some of the commands don't use data
    }
    mUsesData = usesData;
    mBuilder = Dialogs.base(act, R.string.dialog_command);
}

private void chooseCommand(final DialogInterface.OnClickListener listener) {
    offer(mBuilder.setItems(mLabels, listener).create());
}

// Todo: restructure inherits to remove these stubs
@Override
protected void run() {}
@Override
protected void run(final String instruction) {}

@Override
public void execute(final String data) {
    chooseCommand((dialog, which) -> {
        final EmillaCommand cmd = mCommands[which];
        if (cmd.usesData()) ((DataCmd) cmd).execute(data);
        else { // TODO: handle this more gracefully
            cmd.instructAppend(data);
            cmd.execute();
        }
    });
}
}
