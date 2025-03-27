package net.emilla.run;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.exception.EmillaException;
import net.emilla.util.Dialogs;

public final class MessageFailure extends DialogRun {

    public MessageFailure(AssistActivity act, EmillaException e) {
        this(act, e.title, e.message);
    }

    public MessageFailure(AssistActivity act, @StringRes int title, @StringRes int msg) {
        super(act, Dialogs.message(act, title, msg)
                .setNeutralButton(R.string.leave, (dlg, which) -> act.cancel()));
    }

    public MessageFailure(AssistActivity act, CharSequence title, @StringRes int msg) {
        super(act, Dialogs.message(act, title, msg)
                .setNeutralButton(R.string.leave, (dlg, which) -> act.cancel()));
    }

    public MessageFailure(AssistActivity act, CharSequence title, CharSequence msg) {
        super(act, Dialogs.message(act, title, msg)
                .setNeutralButton(R.string.leave, (dlg, which) -> act.cancel()));
    }
}
