package net.emilla.command.app;

import android.view.inputmethod.EditorInfo;

import net.emilla.AssistActivity;
import net.emilla.R;

abstract class VideoSearchBySend extends AppSend {

    private static final class VideoSearchBySendParams extends AppSendParams{

        private VideoSearchBySendParams(Yielder info) {
            super(info,
                  R.string.instruction_video,
                  EditorInfo.IME_ACTION_SEARCH,
                  R.string.summary_video,
                  R.string.manual_app_send);
        }
    }

    VideoSearchBySend(AssistActivity act, Yielder info) {
        super(act, new VideoSearchBySendParams(info));
    }
}
