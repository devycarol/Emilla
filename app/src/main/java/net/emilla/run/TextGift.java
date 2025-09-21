package net.emilla.run;

import androidx.annotation.StringRes;

import net.emilla.activity.AssistActivity;
import net.emilla.util.Dialogs;

public final class TextGift extends DialogRun {

    public TextGift(AssistActivity act, @StringRes int title, @StringRes int msg) {
        super(act, Dialogs.message(act, title, msg));
    }

    public TextGift(AssistActivity act, @StringRes int title, CharSequence msg) {
        super(act, Dialogs.message(act, title, msg));
    }

    public TextGift(AssistActivity act, CharSequence title, @StringRes int msg) {
        super(act, Dialogs.message(act, title, msg));
    }

    public TextGift(AssistActivity act, CharSequence title, CharSequence msg) {
        super(act, Dialogs.message(act, title, msg));
    }
}
