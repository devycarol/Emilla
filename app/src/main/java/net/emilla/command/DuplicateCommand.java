package net.emilla.command;

import android.content.DialogInterface;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.util.Dialogs;

import java.util.Arrays;

public final class DuplicateCommand extends EmillaCommand {

    private final EmillaCommand[] mCommands;
    private final String[] mLabels;

    public DuplicateCommand(
        AssistActivity act,
        CommandYielder[] yielders,
        @Nullable String instruction
    ) {
        super(
            act, new DuplicateParams(),

            R.string.summary_duplicate,
            R.string.manual_duplicate,
            EditorInfo.IME_ACTION_DONE
        );

        mCommands = Arrays.stream(yielders)
            .map(yielder -> yielder.command(act))
            .toArray(EmillaCommand[]::new);
        mLabels = Arrays.stream(mCommands)
            .map(cmd -> cmd.name)
            .toArray(String[]::new);

        instruct(instruction);
    }

    @Override
    protected void run(AssistActivity act) {
        chooseCommand(act, (dlg, which) -> {
            EmillaCommand cmd = mCommands[which];
            cmd.load(act);
            cmd.execute(act);
            cmd.unload(act);
        });
    }

    @Override
    protected void run(AssistActivity act, String instruction) {
        chooseCommand(act, (dlg, which) -> {
            EmillaCommand cmd = mCommands[which];
            cmd.instruct(instruction);
            cmd.load(act);
            cmd.execute(act);
            cmd.unload(act);
        });
    }

    private void chooseCommand(AssistActivity act, DialogInterface.OnClickListener onChoose) {
        offerDialog(act, Dialogs.list(act, R.string.dialog_command, mLabels, onChoose));
    }

}
