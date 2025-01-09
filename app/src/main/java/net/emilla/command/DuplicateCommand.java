package net.emilla.command;

import android.content.DialogInterface;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.command.core.CoreCommand;
import net.emilla.utils.Dialogs;

public class DuplicateCommand extends EmillaCommand implements DataCmd {

    private static class DuplicateParams extends CoreCommand.CoreParams {

        private DuplicateParams() {
            super(R.string.command_duplicate,
                  R.string.instruction_duplicate,
                  R.drawable.ic_command,
                  EditorInfo.IME_ACTION_NEXT);
        }
    }

    @Override @Deprecated
    protected String dupeLabel() {
        // Todo: exclude this from the interface for wrappers
        return null;
    }

    @Override @ArrayRes
    public int details() {
        return R.array.details_duplicate;
    }

    @Override @StringRes
    public final int dataHint() {
        return R.string.data_hint_default;
    }

    @Override
    public final boolean usesData() {
        return mUsesData;
    }

    private final String[] mLabels;
    private final EmillaCommand[] mCommands;
    private final AlertDialog.Builder mBuilder;
    private final boolean mUsesData;

    public DuplicateCommand(AssistActivity act, String instruct, EmillaCommand[] cmds) {
        super(act, instruct, new DuplicateParams());

        mCommands = cmds;
        mLabels = new String[cmds.length];
        boolean usesData = false;
        int i = -1;
        for (EmillaCommand cmd : cmds) {
            if (!usesData && cmd.usesData()) usesData = true;
            mLabels[++i] = cmd.dupeLabel();
            // Todo: whittle down when data is entered and some of the commands don't use data
        }
        mUsesData = usesData;
        mBuilder = Dialogs.listBase(act, R.string.dialog_command);
    }

    private void chooseCommand(DialogInterface.OnClickListener listener) {
        offerDialog(mBuilder.setItems(mLabels, listener));
    }

    // Todo: restructure inherits to remove these stubs
    @Override
    protected void run() {}
    @Override
    protected void run(String instruction) {}

    @Override
    public void execute(String data) {
        chooseCommand((dlg, which) -> {
            EmillaCommand cmd = mCommands[which];
            if (cmd.usesData()) ((DataCmd) cmd).execute(data);
            else { // TODO: handle this more gracefully
                cmd.instructAppend(data);
                cmd.execute();
            }
        });
    }
}
