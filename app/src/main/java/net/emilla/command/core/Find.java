package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

public final class Find extends CoreCommand {

    public static final String ENTRY = "find";
    @StringRes
    public static final int NAME = R.string.command_find;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_find;

    public static Yielder yielder() {
        return new Yielder(true, Find::new, ENTRY, NAME, ALIASES);
    }

    public static boolean possible() {
        return true;
    }

    private Find(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_find,
              R.drawable.ic_find,
              R.string.summary_find,
              R.string.manual_find,
              EditorInfo.IME_ACTION_SEARCH);
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
