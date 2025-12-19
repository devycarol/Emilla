package net.emilla.command.app;

import android.content.Context;

import net.emilla.R;
import net.emilla.annotation.internal;

final class MultilineMessenger extends AppSendData {

    @internal MultilineMessenger(Context ctx, AppEntry appEntry) {
        super(ctx, appEntry, R.string.data_hint_message_cont);
    }

}
