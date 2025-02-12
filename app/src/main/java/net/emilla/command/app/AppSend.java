package net.emilla.command.app;

import static android.content.Intent.EXTRA_TEXT;

import android.content.res.Resources;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.lang.Lang;
import net.emilla.util.Apps;

public class AppSend extends AppCommand {

    private static final class BasicAppSendParams extends AppParams {

        private BasicAppSendParams(Yielder info) {
            super(info,
                  EditorInfo.IME_ACTION_SEND,
                  R.string.summary_app_send,
                  R.string.manual_app_send);
            // todo: the 'send' action shouldn't apply when just launching
        }

        @Override
        public CharSequence title(Resources res) {
            return Lang.colonConcat(res, R.string.command_app_send, name);
        }
    }

    public AppSend(AssistActivity act, Yielder info) {
        this(act, new BasicAppSendParams(info));
    }

    AppSend(AssistActivity act, AppParams params) {
        super(act, params);
    }

    @Override
    protected void run(@NonNull String message) {
        // todo: instantly pull up bookmarked videos for newpipe
        appSucceed(Apps.sendToApp(packageName).putExtra(EXTRA_TEXT, message));
    }

    protected static abstract class AppSendParams extends AppParams {

        @StringRes
        private final int mInstruction;

        protected AppSendParams(Yielder info, @StringRes int instruction, @StringRes int summary,
                @StringRes int manual) {
            this(info, instruction, EditorInfo.IME_ACTION_SEND, summary, manual);
            // todo: the 'send' action shouldn't apply when just launching
        }

        protected AppSendParams(Yielder info, @StringRes int instruction, int imeAction,
                @StringRes int summary, @StringRes int manual) {
            super(info, imeAction, summary, manual);
            mInstruction = instruction;
        }

        @Override
        public final CharSequence title(Resources res) {
            return Lang.colonConcat(res, name, mInstruction);
        }
    }
}
