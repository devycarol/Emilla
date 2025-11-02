package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

/*internal*/ final class Find extends CoreCommand {

    public static final String ENTRY = "find";

    public static boolean possible() {
        return true;
    }

    /*internal*/ Find(AssistActivity act) {
        super(act, CoreEntry.FIND, EditorInfo.IME_ACTION_SEARCH);
    }

    @Override
    protected void run() {
        // todo: select file manager?
        throw badCommand(R.string.error_unfinished_file_search);
    }

    @Override
    protected void run(String fileOrFolder) {
        throw badCommand(R.string.error_unfinished_file_search);
        // where all should I be searching for files? shared storage? external drives?
    }

}
