package net.emilla.command.app;

import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;

/*internal*/ final class VideoSearchBySend extends AppSend {

    /*internal*/ VideoSearchBySend(AssistActivity act, AppEntry appEntry) {
        super(act, appEntry, EditorInfo.IME_ACTION_SEARCH);
    }

}
