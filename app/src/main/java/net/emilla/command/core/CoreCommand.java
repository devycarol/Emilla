package net.emilla.command.core;

import android.content.Context;

import androidx.annotation.StringRes;

import net.emilla.command.EmillaCommand;
import net.emilla.exception.EmillaException;

/*internal*/ abstract class CoreCommand extends EmillaCommand {

    @StringRes
    private final int mName;

    protected CoreCommand(Context ctx, CoreEntry coreEntry, int imeAction) {
        super(ctx, coreEntry, imeAction);

        mName = coreEntry.name;
    }

    protected final EmillaException badCommand(@StringRes int msg) {
        return new EmillaException(mName, msg);
    }

}
