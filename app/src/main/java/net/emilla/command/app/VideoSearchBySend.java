package net.emilla.command.app;

import android.view.inputmethod.EditorInfo;

import net.emilla.AssistActivity;
import net.emilla.R;

/*internal*/ abstract class VideoSearchBySend extends AppSend {

    public VideoSearchBySend(AssistActivity act, Yielder info) {
        super(act, info,
              R.string.instruction_video,
              R.string.summary_video,
              R.string.manual_app_send,
              EditorInfo.IME_ACTION_SEARCH);
    }
}
