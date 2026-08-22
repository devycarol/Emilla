package net.emilla.command.app;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.StringRes;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.annotation.internal;
import net.emilla.annotation.open;
import net.emilla.command.DataCommand;
import net.emilla.wadget.ActionSurface;

@open class AppSendData extends AppSend implements DataCommand {
    @StringRes
    private final int mHint;

    @internal AppSendData(ActionSurface surface, AppEntry appEntry) {
        this(surface, appEntry, R.string.data_hint_text);
    }

    @internal AppSendData(ActionSurface surface, AppEntry appEntry, @StringRes int hint) {
        super(surface, appEntry, EditorInfo.IME_ACTION_NEXT);

        mHint = hint;
    }

    @Override @StringRes
    public final int dataHint() {
        return mHint;
    }

    @Override
    public final Feedback runWithData(ActionSurface surface, String message) {
        return run(surface, message);
    }

    @Override
    public final Feedback runWithData(ActionSurface surface, String message, String cont) {
        return run(surface, message + '\n' + cont);
    }
}
