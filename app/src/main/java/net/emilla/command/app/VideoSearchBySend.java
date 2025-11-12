package net.emilla.command.app;

import android.content.Context;
import android.view.inputmethod.EditorInfo;

/*internal*/ final class VideoSearchBySend extends AppSend {

    /*internal*/ VideoSearchBySend(Context ctx, AppEntry appEntry) {
        super(ctx, appEntry, EditorInfo.IME_ACTION_SEARCH);
    }

}
