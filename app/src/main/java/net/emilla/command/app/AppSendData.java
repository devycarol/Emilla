package net.emilla.command.app;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.command.DataCommand;

/*internal open*/ class AppSendData extends AppSend implements DataCommand {

    @StringRes
    private final int mHint;

    /*internal*/ AppSendData(AssistActivity act, AppEntry appEntry) {
        this(act, appEntry, R.string.data_hint_text);
    }

    /*internal*/ AppSendData(AssistActivity act, AppEntry appEntry, @StringRes int hint) {
        super(act, appEntry, EditorInfo.IME_ACTION_NEXT);

        mHint = hint;
    }

    @Override
    public final boolean usesData() {
        return true;
    }

    @Override @StringRes
    public final int dataHint() {
        return mHint;
    }

    private void runWithData(String message) {
        run(message);
    }

    private void runWithData(String message, String cont) {
        run(message + '\n' + cont);
    }

    @Override
    public final void execute(String data) {
        String instruction = instruction();
        if (instruction != null) {
            runWithData(instruction, data);
        } else {
            runWithData(data);
        }
    }

}
