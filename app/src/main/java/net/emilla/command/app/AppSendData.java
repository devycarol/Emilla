package net.emilla.command.app;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.command.DataCmd;

class AppSendData extends AppSend implements DataCmd {

    private final AppSendDataParams mParams;

    @Override
    public final boolean usesData() {
        return true;
    }

    @Override @StringRes
    public final int dataHint() {
        return mParams.hint();
    }

    AppSendData(AssistActivity act, String instruct, AppSendDataParams params) {
        super(act, instruct, params);
        mParams = params;
    }

    private void runWithData(String message) {
        run(message);
    }

    private void runWithData(String message, String cont) {
        run(message + '\n' + cont);
    }

    @Override
    public void execute(String data) {
        if (instruction == null) runWithData(data);
        else runWithData(instruction, data);
    }

    protected static abstract class AppSendDataParams extends AppSendParams implements DataParams {

        @StringRes
        private final int mHint;

        protected AppSendDataParams(AppInfo info, @StringRes int instruction, @StringRes int summary,
                @StringRes int hint) {
            super(info,
                  instruction,
                  EditorInfo.IME_ACTION_NEXT,
                  summary,
                  R.string.manual_app_send_data);
            mHint = hint;
        }

        @Override @StringRes
        public final int hint() {
            return mHint;
        }
    }
}
