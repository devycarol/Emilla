package net.emilla.command.core;

import android.net.Uri;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.action.Widget;
import net.emilla.action.box.ListFileFragment;
import net.emilla.action.box.TriResult;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.command.ImeAction;
import net.emilla.file.Files;
import net.emilla.util.MimeTypes;
import net.emilla.wadget.ActionSurface;

final class Todo extends EmillaCommand {
    private final ListFileFragment mTodoFragment = ListFileFragment.newInstance();

    @internal Todo(ActionSurface surface) {
        super(surface, CoreEntry.TODO, ImeAction.DO);
    }

    @Override
    protected Widget[] widgets() {
        return new Widget[] {
            mTodoFragment,
        };
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        var cr = surface.getContentResolver();
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
    protected Feedback run(ActionSurface surface, String task) {
        var cr = surface.getContentResolver();
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
