package net.emilla.command;

import android.content.Context;
import android.content.DialogInterface;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.util.Dialogs;

import java.util.Arrays;

/*internal*/ final class DuplicateCommand extends EmillaCommand {

    private final String[] mLabels;
    private final EmillaCommand[] mCommands;

    /*internal*/ DuplicateCommand(Context ctx, EmillaCommand[] cmds) {
        super(
            ctx, new DuplicateParams(),

            R.string.summary_duplicate,
            R.string.manual_duplicate,
            EditorInfo.IME_ACTION_NEXT
        );

        var res = ctx.getResources();
        mLabels = Arrays.stream(cmds)
            .map(cmd -> cmd.name)
            .toArray(String[]::new);
        mCommands = cmds;
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
