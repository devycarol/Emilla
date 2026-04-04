package net.emilla.command.core;

import android.content.Context;

import androidx.annotation.StringRes;

import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;

abstract class CoreCommand extends EmillaCommand {
    @StringRes
    private final int mName;

    @internal CoreCommand(Context ctx, CoreEntry coreEntry, int imeAction) {
        super(ctx, coreEntry, imeAction);

        mName = coreEntry.name;
    }

    protected final void fail(AssistActivity act, @StringRes int msg) {
        act.fail(mName, msg);
    }
}
