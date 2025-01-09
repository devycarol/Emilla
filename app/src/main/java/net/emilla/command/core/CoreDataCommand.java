package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.command.DataCmd;

public abstract class CoreDataCommand extends CoreCommand implements DataCmd {

    @Override
    public final boolean usesData() {
        return true;
    }

    protected static abstract class CoreDataParams extends CoreParams implements DataParams {

        @StringRes
        private final int mHint;

        protected CoreDataParams(@StringRes int name, @StringRes int instruction,
                @DrawableRes int icon, @StringRes int hint) {
            this(name, instruction, true, icon, hint);
        }

        protected CoreDataParams(@StringRes int name, @StringRes int instruction,
                boolean shouldLowercase, @DrawableRes int icon, @StringRes int hint) {
            super(name, instruction, shouldLowercase, icon, EditorInfo.IME_ACTION_NEXT);
            mHint = hint;
        }

        @Override @StringRes
        public int hint() {
            return mHint;
        }
    }

    private final CoreDataParams mParams;

    protected CoreDataCommand(AssistActivity act, String instruct, CoreDataParams params) {
        super(act, instruct, params);
        mParams = params;
    }

    @Override @StringRes
    public final int dataHint() {
        return mParams.hint();
    }

    /**
     * Executes the command with data. This should only be called externally!
     * @param data contents of command data field.
     */
    @Override
    public final void execute(@NonNull String data) {
        if (instruction == null) runWithData(data);
        else runWithData(instruction, data);
    }

    protected abstract void runWithData(String data);
    protected abstract void runWithData(String instruction, String data);
}
