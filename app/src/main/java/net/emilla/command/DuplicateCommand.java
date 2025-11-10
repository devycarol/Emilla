package net.emilla.command;

import android.content.DialogInterface;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.util.Dialogs;

/*internal*/ final class DuplicateCommand extends EmillaCommand implements DataCommand {

    @Override
    protected boolean shouldLowercase() {
        return true; // Todo: exclude this from the interface for wrappers
    }

    @Override @Nullable @Deprecated
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
        super(
            act,
            new DuplicateParams(),
            R.string.summary_duplicate,
            R.string.manual_duplicate,
            EditorInfo.IME_ACTION_NEXT
        );

        int cmdCount = cmds.length;
        mLabels = new String[cmdCount];
        mCommands = cmds;

        boolean usesData = false;
        for (int i = 0; i < cmdCount; ++i) {
            if (!usesData && cmds[i].usesData()) {
                usesData = true;
            }
            mLabels[i] = cmds[i].dupeLabel();
        }

        mUsesData = usesData;
    }

    @Override
    protected void run() {
        chooseCommand((dlg, which) -> {
            mCommands[which].init(this.activity);
            mCommands[which].execute();
            mCommands[which].clean(this.activity);
        });
    }

    @Override
    protected void run(String instruction) {
        chooseCommand((dlg, which) -> {
            mCommands[which].setInstruction(instruction);
            mCommands[which].init(this.activity);
            mCommands[which].execute();
            mCommands[which].clean(this.activity);
        });
    }

    @Override
    public void execute(String data) {
        chooseCommand((dlg, which) -> {
            mCommands[which].init(this.activity);
            // Todo: this surely looks janky and will behave as such. Don't execute dupe commands
            //  immediately, disambiguate prior to execution.
            if (mCommands[which].usesData()) {
                ((DataCommand) mCommands[which]).execute(data);
            } else { // Todo: disambiguate when data is used.
                mCommands[which].instructAppend(data);
                mCommands[which].execute();
            }
            mCommands[which].clean(this.activity);
        });
    }

    private void chooseCommand(DialogInterface.OnClickListener onChoose) {
        offerDialog(Dialogs.list(this.activity, R.string.dialog_command, mLabels, onChoose));
    }

}
