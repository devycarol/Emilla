package net.emilla.command.core;

import static net.emilla.chime.Chime.PEND;

import android.content.Context;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.action.box.TodoFragment;
import net.emilla.activity.AssistActivity;
import net.emilla.exception.EmillaException;
import net.emilla.file.Files;
import net.emilla.util.MimeTypes;

/*internal*/ final class Todo extends CoreCommand {

    private final TodoFragment mTodoFragment;

    /*internal*/ Todo(Context ctx) {
        super(ctx, CoreEntry.TODO, EditorInfo.IME_ACTION_DONE);

        mTodoFragment = TodoFragment.newInstance();
        giveGadgets(mTodoFragment);
    }

    @Override
    protected void run(AssistActivity act) {
        Uri file = mTodoFragment.file();
        if (file != null) {
            appSucceed(act, Files.viewIntent(file, MimeTypes.PLAIN_TEXT));
        } else {
            act.chime(PEND);
        }
    }

    @Override
    protected void run(AssistActivity act, String task) {
        switch (mTodoFragment.addTask(task)) {
        case SUCCESS -> act.give(a -> {});
        case WAITING -> act.chime(PEND);
        case FAILURE -> throw new EmillaException(R.string.error_cant_use_file);
        }
    }

}
