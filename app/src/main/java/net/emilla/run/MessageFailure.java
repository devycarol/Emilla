package net.emilla.run;

import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Dialogs;

public class MessageFailure extends DialogFailure {

    public MessageFailure(AssistActivity act, @StringRes int title, @StringRes int msg) {
        super(act, Dialogs.baseCancel(act, title, msg)
                .setNeutralButton(R.string.leave, (dialog, which) -> act.cancel()));
    }
}
