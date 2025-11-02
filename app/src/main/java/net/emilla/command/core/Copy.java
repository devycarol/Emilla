package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.run.CopyGift;

public final class Copy extends CoreCommand {

    public static final String ENTRY = "copy";

    public static Yielder yielder() {
        return new Yielder(CoreEntry.COPY, true);
    }

    public static boolean possible() {
        return true;
    }

    @Nullable
    private String mCopiedText = null;

    /*internal*/ Copy(AssistActivity act) {
        super(act, CoreEntry.COPY, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void onClean() {
        super.onClean();
        mCopiedText = null;
        // forget the copied text
    }

    @Override
    protected void run() {
        throw badCommand(R.string.error_unfinished_copy);
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
        give(new CopyGift(text));
    }
}
