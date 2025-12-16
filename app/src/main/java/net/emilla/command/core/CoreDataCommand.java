package net.emilla.command.core;

import android.content.Context;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.StringRes;

import net.emilla.command.DataCommand;

abstract class CoreDataCommand extends CoreCommand implements DataCommand {

    @StringRes
    private final int mHint;

    /*internal*/ CoreDataCommand(Context ctx, CoreEntry coreEntry, @StringRes int dataHint) {
        super(ctx, coreEntry, EditorInfo.IME_ACTION_NEXT);
        mHint = dataHint;
    }

    @Override @StringRes
    public final int dataHint() {
        return mHint;
    }

}
