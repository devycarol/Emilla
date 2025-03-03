package net.emilla.run;

import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.util.Dialogs;

public final class MessageGift extends DialogRun {

    public MessageGift(AssistActivity act, @StringRes int title, @StringRes int msg) {
        super(act, Dialogs.message(act, title, msg));
    }

    public MessageGift(AssistActivity act, @StringRes int title, CharSequence msg) {
        super(act, Dialogs.message(act, title, msg));
    }

    public MessageGift(AssistActivity act, CharSequence title, @StringRes int msg) {
        super(act, Dialogs.message(act, title, msg));
    }

    public MessageGift(AssistActivity act, CharSequence title, CharSequence msg) {
        super(act, Dialogs.message(act, title, msg));
    }
}
