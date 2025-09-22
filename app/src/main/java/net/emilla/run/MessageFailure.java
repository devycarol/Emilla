package net.emilla.run;

import android.content.Context;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.exception.EmillaException;
import net.emilla.util.Dialogs;

public final class MessageFailure extends DialogRun {

    public MessageFailure(Context ctx, EmillaException e) {
        this(ctx, e.title, e.message);
    }

    public MessageFailure(Context ctx, @StringRes int title, @StringRes int msg) {
        super(Dialogs.message(ctx, title, msg));
    }

    public MessageFailure(Context ctx, @StringRes int title, CharSequence msg) {
        super(Dialogs.message(ctx, title, msg));
    }

    public MessageFailure(Context ctx, CharSequence title, @StringRes int msg) {
        super(Dialogs.message(ctx, title, msg));
    }

    public MessageFailure(Context ctx, CharSequence title, CharSequence msg) {
        super(Dialogs.message(ctx, title, msg));
    }

    @Override
    public void run(AssistActivity act) {
        dialog.setNeutralButton(R.string.leave, (dlg, which) -> act.cancel());
        super.run(act);
    }
}
