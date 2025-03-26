package net.emilla.command.app;

import static android.content.Intent.EXTRA_TEXT;

import android.content.res.Resources;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.lang.Lang;
import net.emilla.app.Apps;

public /*open*/ class AppSend extends AppCommand {

    private static final class AppSendParams extends AppParams {

        AppSendParams(Yielder info) {
            super(info);
        }

        @Override
        public CharSequence title(Resources res) {
            return Lang.colonConcat(res, R.string.command_app_send, name);
        }
    }

    public AppSend(AssistActivity act, Yielder info) {
        this(act, info,
             R.string.summary_app_send,
             R.string.manual_app_send,
             EditorInfo.IME_ACTION_SEND);
        // todo: the 'send' action shouldn't apply when just launching
    }

    AppSend(
        AssistActivity act,
        Yielder info,
        @StringRes int summary,
        @StringRes int manual,
        int imeAction
    ) {
        // for the generics
        super(act, new AppSendParams(info), summary, manual, imeAction);
    }

    AppSend(AssistActivity act, Yielder info, @StringRes int instruction, @StringRes int summary) {
        this(act, info,
             instruction,
             summary,
             R.string.manual_app_send,
             EditorInfo.IME_ACTION_SEND);
    }

    AppSend(
        AssistActivity act,
        Yielder info,
        @StringRes int instruction,
        @StringRes int summary,
        @StringRes int manual,
        int imeAction
    ) {
        super(act, new InstructyParams(info, instruction),
              summary,
              manual,
              imeAction);
    }

    @Override
    protected final void run(String message) {
        appSucceed(Apps.sendToApp(packageName).putExtra(EXTRA_TEXT, message));
    }
}
