package net.emilla.command.app;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.command.DataCommand;

/*internal open*/ class AppSendData extends AppSend implements DataCommand {

    @StringRes
    private final int mHint;

    public AppSendData(AssistActivity act, Yielder info, @StringRes int hint) {
        super(act, info,
              R.string.summary_app_send,
              R.string.manual_app_send_data,
              EditorInfo.IME_ACTION_NEXT);
        mHint = hint;
    }

    protected AppSendData(
        AssistActivity act,
        Yielder info,
        @StringRes int instruction,
        @StringRes int summary,
        @StringRes int hint
    ) {
        super(act, info,
              instruction,
              summary,
              R.string.manual_app_send_data,
              EditorInfo.IME_ACTION_NEXT);
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
