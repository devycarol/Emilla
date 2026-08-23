package net.emilla.command;

import android.content.DialogInterface;

import androidx.annotation.Nullable;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.util.Dialogs;
import net.emilla.wadget.ActionSurface;

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
            act,
            new DuplicateParams(),
            R.string.summary_duplicate,
            R.string.manual_duplicate,
            ImeAction.DO
        );

        mCommands = Arrays.stream(yielders)
            .map(yielder -> yielder.command(act))
            .toArray(EmillaCommand[]::new)
        ;
        mLabels = Arrays.stream(mCommands)
            .map(cmd -> cmd.name)
            .toArray(String[]::new)
        ;
        instruct(act, instruction);
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        var act = surface.getAssistActivity();
        return chooseCommand(act, (dlg, which) -> {
            EmillaCommand cmd = mCommands[which];
            cmd.load(act);
            surface.take(cmd.execute(surface));
            cmd.unload(act);
        });
    }

    @Override
    protected Feedback run(ActionSurface surface, String instruction) {
        var act = surface.getAssistActivity();
        return chooseCommand(act, (dlg, which) -> {
            EmillaCommand cmd = mCommands[which];
            cmd.instruct(act, instruction);
            cmd.load(act);
            surface.take(cmd.execute(surface));
            cmd.unload(act);
        });
    }

    private Feedback chooseCommand(
        AssistActivity act,
        DialogInterface.OnClickListener onChoose
    ) {
        return Feedback.offer(
            Dialogs.list(act, R.string.dialog_command, mLabels, onChoose)
        );
    }
}
