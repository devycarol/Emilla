package net.emilla.command.core;

import static net.emilla.chime.Chime.PEND;

import android.content.Context;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.action.box.ListFileFragment;
import net.emilla.action.box.TriResult;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.file.Files;
import net.emilla.util.Apps;
import net.emilla.util.MimeTypes;

final class Todo extends CoreCommand {
    private final ListFileFragment mTodoFragment;

    @internal Todo(Context ctx) {
        super(ctx, CoreEntry.TODO, EditorInfo.IME_ACTION_DONE);

        mTodoFragment = ListFileFragment.newInstance();
        giveGadgets(mTodoFragment);
    }

    @Override
    protected void run(AssistActivity act) {
        var cr = act.getContentResolver();
        TriResult result = mTodoFragment.completeSelection(cr);
        if (result != null) {
            actionFeedback(act, result);
            return;
        }

        Uri file = mTodoFragment.file();
        if (file != null) {
            Apps.succeed(act, Files.viewIntent(file, MimeTypes.PLAIN_TEXT));
        } else {
            act.chime(PEND);
        }
    }

    @Override
    protected void run(AssistActivity act, String task) {
        var cr = act.getContentResolver();
        TriResult result = mTodoFragment.completeSelection(cr);
        if (result != null) {
            actionFeedback(act, result);
            return;
        }

        actionFeedback(act, mTodoFragment.addTask(cr, task));
    }

    private void actionFeedback(AssistActivity act, TriResult result) {
        switch (result) {
        case SUCCESS -> act.selectInstruction();
        case WAITING -> act.chime(PEND);
        case FAILURE -> fail(act, R.string.error_cant_use_file);
        }
    }
}
