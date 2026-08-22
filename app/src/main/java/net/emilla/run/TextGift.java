package net.emilla.run;

import android.content.Context;

import net.emilla.Feedback;
import net.emilla.activity.AssistActivity;
import net.emilla.util.Clipboard;
import net.emilla.util.Dialogs;

public final class TextGift extends DialogRun {
    private final CharSequence mText;

    public TextGift(Context ctx, CharSequence title, CharSequence text) {
        super(Dialogs.message(ctx, title, text));
        mText = text;
    }

    @Override
    public void run(AssistActivity act) {
        this.dialog.setNeutralButton(android.R.string.copy, (dlg, which) -> {
            act.onCloseDialog(); // Todo: don't require this.
            Clipboard.copy(act, mText);
            act.take(Feedback.give());
        });
        super.run(act);
    }
}
