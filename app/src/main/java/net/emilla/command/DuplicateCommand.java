package net.emilla.command;

import android.content.DialogInterface;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.command.core.CoreCommand.CoreParams;
import net.emilla.util.Dialogs;

public final class DuplicateCommand extends EmillaCommand implements DataCommand {

    @Override
    protected boolean shouldLowercase() {
        return true; // Todo: exclude this from the interface for wrappers
    }

    @Override @Deprecated
    protected String dupeLabel() {
        return null; // Todo: exclude this from the interface for wrappers
    }

    @Override
    protected boolean usesAppIcon() {
        return false;
    }

    @Override @StringRes
    public int dataHint() {
        return R.string.data_hint_default;
    }

    @Override
    public boolean usesData() {
        return mUsesData;
    }

    private final String[] mLabels;
    private final EmillaCommand[] mCommands;
    private final boolean mUsesData;

    public DuplicateCommand(AssistActivity act, EmillaCommand[] cmds) {
        super(act, new CoreParams(R.string.command_duplicate,
                                  R.string.instruction_duplicate,
                                  R.drawable.ic_command),
              R.string.summary_duplicate,
              R.string.manual_duplicate,
              EditorInfo.IME_ACTION_NEXT);

        mLabels = new String[cmds.length];
        mCommands = cmds;
        boolean usesData = false;
        for (int i = 0; i < cmds.length; ++i) {
            if (!usesData && cmds[i].usesData()) usesData = true;
            mLabels[i] = cmds[i].dupeLabel();
        }
        mUsesData = usesData;
    }

    @Override
    protected void run() {
        chooseCommand((dlg, which) -> {
            mCommands[which].init();
            mCommands[which].execute();
            mCommands[which].clean();
        });
    }

    @Override
    protected void run(@NonNull String instruction) {
        chooseCommand((dlg, which) -> {
            mCommands[which].setInstruction(instruction);
            mCommands[which].init();
            mCommands[which].execute();
            mCommands[which].clean();
        });
    }

    @Override
    public void execute(@NonNull String data) {
        chooseCommand((dlg, which) -> {
            mCommands[which].init();
            // Todo: this surely looks janky and will behave as such. Don't execute dupe commands
            //  immediately, disambiguate prior to execution.
            if (mCommands[which].usesData()) ((DataCommand) mCommands[which]).execute(data);
            else { // Todo: disambiguate when data is used.
                mCommands[which].instructAppend(data);
                mCommands[which].execute();
            }
            mCommands[which].clean();
        });
    }

    private void chooseCommand(DialogInterface.OnClickListener onChoose) {
        offerDialog(Dialogs.list(activity, R.string.dialog_command, mLabels, onChoose));
    }
}
