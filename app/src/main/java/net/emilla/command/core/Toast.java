package net.emilla.command.core;

import android.content.Context;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.run.ToastGift;

/*internal*/ final class Toast extends CoreDataCommand {

    /*internal*/ Toast(Context ctx) {
        super(ctx, CoreEntry.TOAST, R.string.data_hint_toast);
    }

    private static void toast(AssistActivity act, String message, boolean isLongToast) {
        act.give(new ToastGift(message, isLongToast));
    }

    @Override
    protected void run(AssistActivity act) { // todo: configurable default message
        var res = act.getResources();
        toast(act, res.getString(R.string.toast_hello), false);
    }

    @Override
    protected void run(AssistActivity act, String message) {
        var res = act.getResources();
        String longTag = res.getString(R.string.tag_toast_long); // todo: change this to a button
        if (message.toLowerCase().endsWith(longTag)) {
            message = message.substring(0, message.length() - longTag.length()).trim();
            toast(act, message.isEmpty() ? res.getString(R.string.toast_hello) : message, true);
            // todo: configurable default message
        } else {
            toast(act, message, false);
        }
    }

    @Override
    public void runWithData(AssistActivity act, String message) {
        var res = act.getResources();
        String longTag = res.getString(R.string.tag_toast_long); // todo way later make configurable
        if (message.toLowerCase().endsWith(longTag)) {
            message = message.substring(0, message.length() - longTag.length()).trim();
            toast(act, message.isEmpty() ? res.getString(R.string.toast_hello) : message, true);
            // todo: configurable default message
        } else {
            toast(act, message, false);
        }
    }

    @Override
    public void runWithData(AssistActivity act, String message, String cont) {
        var res = act.getResources();
        String longTag = res.getString(R.string.tag_toast_long); // todo way later make configurable
        if (message.toLowerCase().endsWith(longTag)) {
            message = message.substring(0, message.length() - longTag.length()).trim() + '\n' + cont;
            toast(act, message, true);
        } else if (cont.toLowerCase().endsWith(longTag)) {
            message = message + '\n' + cont.substring(0, message.length() - longTag.length());
            toast(act, message, true);
        } else {
            toast(act, message + '\n' + cont, false);
        }
    }

}
