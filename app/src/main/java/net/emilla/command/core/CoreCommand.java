package net.emilla.command.core;

import androidx.annotation.StringRes;

import net.emilla.activity.AssistActivity;
import net.emilla.command.EmillaCommand;
import net.emilla.exception.EmillaException;

/*internal*/ abstract class CoreCommand extends EmillaCommand {

    @StringRes
    private final int mName;

    protected CoreCommand(AssistActivity act, CoreEntry coreEntry, int imeAction) {
        super(act, coreEntry, imeAction);

        mName = coreEntry.name;
    }

    @Override
    protected /*open*/ boolean shouldLowercase() {
        return true;
    }

    @Override
    public final boolean usesAppIcon() {
        return false;
    }

    @Override @Deprecated
    protected final String dupeLabel() {
        return str(mName) + " (Emilla command)";
    }

    protected final EmillaException badCommand(@StringRes int msg) {
        return new EmillaException(mName, msg);
    }

}
