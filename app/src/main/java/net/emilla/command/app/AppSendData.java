package net.emilla.command.app;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.command.DataCommand;

class AppSendData extends AppSend implements DataCommand {

    @StringRes
    private final int mHint;

    public AppSendData(AssistActivity act, Yielder info, @StringRes int hint) {
        super(act, info,
              R.string.summary_app_send,
              R.string.manual_app_send_data,
              EditorInfo.IME_ACTION_NEXT);
        mHint = hint;
    }

    AppSendData(
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
    public boolean usesData() {
        return true;
    }

    @Override @StringRes
    public int dataHint() {
        return mHint;
    }

    private void runWithData(@NonNull String message) {
        run(message);
    }

    private void runWithData(@NonNull String message, String cont) {
        run(message + '\n' + cont);
    }

    @Override
    public void execute(@NonNull String data) {
        String instruction = instruction();
        if (instruction == null) runWithData(data);
        else runWithData(instruction, data);
    }
}
