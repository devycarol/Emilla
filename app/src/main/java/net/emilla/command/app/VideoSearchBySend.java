package net.emilla.command.app;

import android.view.inputmethod.EditorInfo;

import net.emilla.AssistActivity;
import net.emilla.R;

abstract class VideoSearchBySend extends AppSend {

    private static class VideoSearchBySendParams extends AppSendParams{

        private VideoSearchBySendParams(AppInfo info) {
            super(info,
                  R.string.instruction_video,
                  EditorInfo.IME_ACTION_SEARCH,
                  R.string.summary_video,
                  R.string.manual_app_send);
        }
    }

    VideoSearchBySend(AssistActivity act, String instruct, AppInfo info) {
        super(act, instruct, new VideoSearchBySendParams(info));
    }
}
