package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;
import net.emilla.settings.Aliases;

public class Find extends CoreCommand {

    public static final String ENTRY = "find";
    @StringRes
    public static final int NAME = R.string.command_find;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_find;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Find::new, ENTRY, NAME, ALIASES);
    }

    private static class FindParams extends CoreParams {

        private FindParams() {
            super(NAME,
                  R.string.instruction_find,
                  R.drawable.ic_find,
                  EditorInfo.IME_ACTION_SEARCH,
                  R.string.summary_find,
                  R.string.manual_find);
        }
    }

    public Find(AssistActivity act) {
        super(act, new FindParams());
    }

    @Override
    protected void run() {
        // todo: select file manager?
        throw new EmlaBadCommandException(NAME, R.string.error_unfinished_file_search);
    }

    @Override
    protected void run(String fileOrFolder) {
        throw new EmlaBadCommandException(NAME, R.string.error_unfinished_file_search);
        // where all should I be searching for files? shared storage? external drives?
    }
}
