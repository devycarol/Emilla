package net.emilla.command.core;

import android.content.Context;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.action.box.ListFileFragment;
import net.emilla.action.box.TriResult;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.file.Files;
import net.emilla.util.MimeTypes;
import net.emilla.wadget.ActionSurface;

final class Todo extends EmillaCommand {
    private final ListFileFragment mTodoFragment;

    @internal Todo(Context ctx) {
        super(ctx, CoreEntry.TODO, EditorInfo.IME_ACTION_DONE);

        mTodoFragment = ListFileFragment.newInstance();
        giveGadgets(mTodoFragment);
    }

    @Override
    protected Feedback run(ActionSurface surface, AssistActivity act) {
        var cr = act.getContentResolver();
        TriResult result = mTodoFragment.completeSelection(cr);
        if (result != null) {
            return actionFeedback(result);
        }

        Uri file = mTodoFragment.file();
        return file != null
            ? Feedback.succeed(Files.viewIntent(file, MimeTypes.PLAIN_TEXT))
            : Feedback.pend()
        ;
    }

    @Override
    protected Feedback run(ActionSurface surface, AssistActivity act, String task) {
        var cr = act.getContentResolver();
        TriResult result = mTodoFragment.completeSelection(cr);
        return result != null
            ? actionFeedback(result)
            : actionFeedback(mTodoFragment.addTask(cr, task))
        ;
    }

    private static Feedback actionFeedback(TriResult result) {
        return switch (result) {
            case SUCCESS -> Feedback.selectInstruction();
            case WAITING -> Feedback.pend();
            case FAILURE -> Feedback.fail(R.string.error_cant_use_file);
        };
    }
}
