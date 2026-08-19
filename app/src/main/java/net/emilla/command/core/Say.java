package net.emilla.command.core;

import android.content.Context;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.run.ToastGift;

final class Say extends CoreCommand {
    @internal Say(Context ctx) {
        super(ctx, CoreEntry.SAY, R.string.data_hint_say);
    }

    private static void say(AssistActivity act, CharSequence message) {
        act.give(ToastGift.instance(message, false));
    }

    @Override
    protected void run(AssistActivity act) {
        var res = act.getResources();
        say(act, res.getString(R.string.toast_hello));
    }

    @Override
    protected void run(AssistActivity act, String message) {
        say(act, message);
    }
}
