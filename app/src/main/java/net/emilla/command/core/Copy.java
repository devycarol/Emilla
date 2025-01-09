package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;
import net.emilla.run.CopyGift;

public class Copy extends CoreCommand {

    public static final String ENTRY = "copy";

    private static class CopyParams extends CoreParams {

        private CopyParams() {
            super(R.string.command_copy,
                  R.string.instruction_text,
                  R.drawable.ic_copy,
                  EditorInfo.IME_ACTION_DONE);
        }
    }

    private String mCopiedText;

    public Copy(AssistActivity act, String instruct) {
        super(act, instruct, new CopyParams());
    }

    @Override
    public void clean() {
        super.clean();
        mCopiedText = null;
        // forget the copied text
    }

    @Override
    protected void run() {
        throw new EmlaBadCommandException(R.string.command_copy, R.string.error_unfinished_copy);
        // Todo
    }

    @Override
    protected void run(String text) {
        if (text.equals(mCopiedText)) {
            // todo: you could change the submit icon to indicate this behavior. it would require
            //  monitoring text changes and updating the icon each time the user types. if the
            //  instruction is the already-copied text, set the close icon. otherwise, set/keep the
            //  copy icon. you could even query the system clipboard instead for this check, and
            //  listen for copy events that change what the behavior & icon should be.
            succeed();
            return;
        }
        mCopiedText = text;
        give(new CopyGift(activity, text));
    }
}
