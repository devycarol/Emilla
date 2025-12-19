package net.emilla.command.core;

import android.content.Context;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.run.CopyGift;

final class Copy extends CoreCommand {

    @Nullable
    private String mCopiedText = null;

    @internal Copy(Context ctx) {
        super(ctx, CoreEntry.COPY, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run(AssistActivity act) {
        throw badCommand(R.string.error_unfinished_copy);
        // Todo
    }

    @Override
    protected void run(AssistActivity act, String text) {
        if (text.equals(mCopiedText)) {
            // todo: you could change the submit icon to indicate this behavior. it would require
            //  monitoring text changes and updating the icon each time the user types. if the
            //  instruction is the already-copied text, set the close icon. otherwise, set/keep the
            //  copy icon. you could even query the system clipboard instead for this check, and
            //  listen for copy events that change what the behavior & icon should be.
            succeed(act);
            return;
        }
        mCopiedText = text;
        act.give(new CopyGift(text));
    }

    @Override
    public void unload(AssistActivity act) {
        super.unload(act);
        mCopiedText = null;
        // forget the copied text
    }

}
