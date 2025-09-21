package net.emilla.run;

import androidx.annotation.StringRes;

import net.emilla.activity.AssistActivity;
import net.emilla.util.Dialogs;

public final class TextGift extends DialogRun {

    public TextGift(AssistActivity act, @StringRes int title, @StringRes int text) {
        super(act, Dialogs.message(act, title, text)
            .setNeutralButton(android.R.string.copy, (dlg, which) -> {
                act.onCloseDialog(); // Todo: don't require this.
                act.give(new CopyGift(act, text));
            }));
    }

    public TextGift(AssistActivity act, @StringRes int title, CharSequence text) {
        super(act, Dialogs.message(act, title, text)
            .setNeutralButton(android.R.string.copy, (dlg, which) -> {
                act.onCloseDialog(); // Todo: don't require this.
                act.give(new CopyGift(act, text));
            }));
    }

    public TextGift(AssistActivity act, CharSequence title, @StringRes int text) {
        super(act, Dialogs.message(act, title, text)
            .setNeutralButton(android.R.string.copy, (dlg, which) -> {
                act.onCloseDialog(); // Todo: don't require this.
                act.give(new CopyGift(act, text));
            }));
    }

    public TextGift(AssistActivity act, CharSequence title, CharSequence text) {
        super(act, Dialogs.message(act, title, text)
            .setNeutralButton(android.R.string.copy, (dlg, which) -> {
                act.onCloseDialog(); // Todo: don't require this.
                act.give(new CopyGift(act, text));
            }));
    }
}
