package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.run.CopyGift;
import net.emilla.settings.Aliases;

public final class Copy extends CoreCommand {

    public static final String ENTRY = "copy";
    @StringRes
    public static final int NAME = R.string.command_copy;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_copy;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Copy::new, ENTRY, NAME, ALIASES);
    }

    @Nullable
    private String mCopiedText;

    private Copy(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_text,
              R.drawable.ic_copy,
              R.string.summary_copy,
              R.string.manual_copy,
              EditorInfo.IME_ACTION_DONE);
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
        give(new CopyGift(activity, text));
    }
}
