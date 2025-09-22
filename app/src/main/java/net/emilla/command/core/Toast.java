package net.emilla.command.core;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.run.ToastGift;

public final class Toast extends CoreDataCommand {

    public static final String ENTRY = "toast";
    @StringRes
    public static final int NAME = R.string.command_toast;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_toast;

    public static Yielder yielder() {
        return new Yielder(true, Toast::new, ENTRY, NAME, ALIASES);
    }

    public static boolean possible() {
        return true;
    }

    private Toast(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_text,
              R.drawable.ic_toast,
              R.string.summary_toast,
              R.string.manual_toast,
              R.string.data_hint_toast);
    }

    private void toast(String message, boolean longToast) {
        give(new ToastGift(message, longToast));
    }

    @Override
    protected void run() { // todo: configurable default message
        toast(str(R.string.toast_hello), false);
    }

    @Override
    protected void run(String message) {
        String longTag = str(R.string.tag_toast_long); // todo: change this to a button
        if (message.toLowerCase().endsWith(longTag)) {
            message = message.substring(0, message.length() - longTag.length()).trim();
            toast(message.isEmpty() ? str(R.string.toast_hello) : message, true); // todo: configurable default message
        } else toast(message, false);
    }

    @Override
    protected void runWithData(String message) {
        String longTag = str(R.string.tag_toast_long); // todo way later make configurable
        if (message.toLowerCase().endsWith(longTag)) {
            message = message.substring(0, message.length() - longTag.length()).trim();
            toast(message.isEmpty() ? str(R.string.toast_hello) : message, true); // todo: configurable default message
        } else toast(message, false);
    }

    @Override
    protected void runWithData(String message, String cont) {
        String longTag = str(R.string.tag_toast_long); // todo way later make configurable
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
