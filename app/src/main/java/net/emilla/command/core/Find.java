package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.DrawableRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;

public class Find extends CoreCommand {

    public Find(AssistActivity act, String instruct) {
        super(act, instruct, R.string.command_find, R.string.instruction_find);
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_find;
    }

    @Override
    public int imeAction() {
        return EditorInfo.IME_ACTION_SEARCH;
    }

    @Override
    protected void run() {
        // todo: select file manager?
        throw new EmlaBadCommandException("Sorry! I don't know how to search for files yet.");
    }

    @Override
    protected void run(String fileOrFolder) {
        throw new EmlaBadCommandException("Sorry! I don't know how to search for files yet.");
        // where all should I be searching for files? shared storage? external drives?
    }
}
