package net.emilla.run;

import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.util.Dialogs;

public class MessageFailure extends DialogFailure {

    public MessageFailure(AssistActivity act, @StringRes int title, @StringRes int msg) {
        super(act, Dialogs.base(act, title, msg, android.R.string.ok)
                .setNeutralButton(R.string.leave, (dlg, which) -> act.cancel()));
    }

    public MessageFailure(AssistActivity act, @StringRes int title, CharSequence msg) {
        super(act, Dialogs.base(act, title, msg, android.R.string.ok)
                .setNeutralButton(R.string.leave, (dlg, which) -> act.cancel()));
    }
}
