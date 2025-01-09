package net.emilla.command.core;

import androidx.annotation.ArrayRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.run.ToastGift;

public class Toast extends CoreDataCommand {

    public static final String ENTRY = "toast";

    private static class ToastParams extends CoreDataParams {

        private ToastParams() {
            super(R.string.command_toast,
                  R.string.instruction_text,
                  R.drawable.ic_toast,
                  R.string.data_hint_toast);
        }
    }

    @Override @ArrayRes
    public int details() {
        return R.array.details_toast;
    }

    public Toast(AssistActivity act, String instruct) {
        super(act, instruct, new ToastParams());
    }

    private void toast(String message, boolean longToast) {
        give(new ToastGift(activity, message, longToast));
    }

    @Override
    protected void run() { // todo: configurable default message
        toast(string(R.string.toast_hello), false);
    }

    @Override
    protected void run(String message) {
        String longTag = string(R.string.tag_toast_long); // todo: change this to a button
        if (message.toLowerCase().endsWith(longTag)) {
            message = message.substring(0, message.length() - longTag.length()).trim();
            toast(message.isEmpty() ? string(R.string.toast_hello) : message, true); // todo: configurable default message
        } else toast(message, false);
    }

    @Override
    protected void runWithData(String message) {
        String longTag = string(R.string.tag_toast_long); // todo way later make configurable
        if (message.toLowerCase().endsWith(longTag)) {
            message = message.substring(0, message.length() - longTag.length()).trim();
            toast(message.isEmpty() ? string(R.string.toast_hello) : message, true); // todo: configurable default message
        } else toast(message, false);
    }

    @Override
    protected void runWithData(String message, String cont) {
        String longTag = string(R.string.tag_toast_long); // todo way later make configurable
        if (message.toLowerCase().endsWith(longTag)) {
            message = message.substring(0, message.length() - longTag.length()).trim() + '\n' + cont;
            toast(message, true);
        } else if (cont.toLowerCase().endsWith(longTag)) {
            message = message + '\n' + cont.substring(0, message.length() - longTag.length());
            toast(message, true);
        }
        else toast(message + '\n' + cont, false);
    }
}
