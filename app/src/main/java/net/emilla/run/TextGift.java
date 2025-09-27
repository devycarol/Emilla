package net.emilla.run;

import android.content.Context;

import androidx.annotation.StringRes;

import net.emilla.activity.AssistActivity;
import net.emilla.util.Dialogs;

public final class TextGift extends DialogRun {

    private final CharSequence mText;

    public TextGift(Context ctx, @StringRes int title, @StringRes int text) {
        super(Dialogs.message(ctx, title, text));
        mText = ctx.getString(text);
    }

    public TextGift(Context ctx, @StringRes int title, CharSequence text) {
        super(Dialogs.message(ctx, title, text));
        mText = text;
    }

    public TextGift(Context ctx, CharSequence title, @StringRes int text) {
        super(Dialogs.message(ctx, title, text));
        mText = ctx.getString(text);
    }

    public TextGift(Context ctx, CharSequence title, CharSequence text) {
        super(Dialogs.message(ctx, title, text));
        mText = text;
    }

    @Override
    public void run(AssistActivity act) {
        pDialog.setNeutralButton(android.R.string.copy, (dlg, which) -> {
            act.onCloseDialog(); // Todo: don't require this.
            act.give(new CopyGift(mText));
        });
        super.run(act);
    }
}
