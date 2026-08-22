package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.StringRes;

import net.emilla.annotation.internal;
import net.emilla.command.DataCommand;
import net.emilla.command.EmillaCommand;
import net.emilla.wadget.ActionSurface;

abstract class CoreDataCommand extends EmillaCommand implements DataCommand {
    @StringRes
    private final int mHint;

    @internal CoreDataCommand(
        ActionSurface surface,
        CoreEntry coreEntry,
        @StringRes int dataHint
    ) {
        super(surface, coreEntry, EditorInfo.IME_ACTION_NEXT);
        mHint = dataHint;
    }

    @Override @StringRes
    public final int dataHint() {
        return mHint;
    }
}
