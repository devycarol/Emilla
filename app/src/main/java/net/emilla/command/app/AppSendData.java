package net.emilla.command.app;

import android.content.Context;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.command.DataCommand;

/*open*/ class AppSendData extends AppSend implements DataCommand {

    @StringRes
    private final int mHint;

    /*internal*/ AppSendData(Context ctx, AppEntry appEntry) {
        this(ctx, appEntry, R.string.data_hint_text);
    }

    /*internal*/ AppSendData(Context ctx, AppEntry appEntry, @StringRes int hint) {
        super(ctx, appEntry, EditorInfo.IME_ACTION_NEXT);

        mHint = hint;
    }

    @Override @StringRes
    public final int dataHint() {
        return mHint;
    }

    @Override
    public final void runWithData(AssistActivity act, String message) {
        run(act, message);
    }

    @Override
    public final void runWithData(AssistActivity act, String message, String cont) {
        run(act, message + '\n' + cont);
    }

}
