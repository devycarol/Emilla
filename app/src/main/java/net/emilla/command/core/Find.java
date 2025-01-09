package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;

public class Find extends CoreCommand {

    public static final String ENTRY = "find";

    private static class FindParams extends CoreParams {

        private FindParams() {
            super(R.string.command_find,
                  R.string.instruction_find,
                  R.drawable.ic_find,
                  EditorInfo.IME_ACTION_SEARCH);
        }
    }

    public Find(AssistActivity act, String instruct) {
        super(act, instruct, new FindParams());
    }

    @Override
    protected void run() {
        // todo: select file manager?
        throw new EmlaBadCommandException(R.string.command_find, R.string.error_unfinished_file_search);
    }

    @Override
    protected void run(String fileOrFolder) {
        throw new EmlaBadCommandException(R.string.command_find, R.string.error_unfinished_file_search);
        // where all should I be searching for files? shared storage? external drives?
    }
}
