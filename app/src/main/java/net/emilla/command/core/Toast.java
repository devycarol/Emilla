package net.emilla.command.core;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.run.ToastGift;
import net.emilla.settings.Aliases;

public final class Toast extends CoreDataCommand {

    public static final String ENTRY = "toast";
    @StringRes
    public static final int NAME = R.string.command_toast;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_toast;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Toast::new, ENTRY, NAME, ALIASES);
    }

    private static final class ToastParams extends CoreDataParams {

        private ToastParams() {
            super(NAME,
                  R.string.instruction_text,
                  R.drawable.ic_toast,
                  R.string.summary_toast,
                  R.string.manual_toast,
                  R.string.data_hint_toast);
        }
    }

    public Toast(AssistActivity act) {
        super(act, new ToastParams());
    }

    private void toast(String message, boolean longToast) {
        give(new ToastGift(activity, message, longToast));
    }

    @Override
    protected void run() { // todo: configurable default message
        toast(string(R.string.toast_hello), false);
    }

    @Override
    protected void run(@NonNull String message) {
        var longTag = string(R.string.tag_toast_long); // todo: change this to a button
        if (message.toLowerCase().endsWith(longTag)) {
            message = message.substring(0, message.length() - longTag.length()).trim();
            toast(message.isEmpty() ? string(R.string.toast_hello) : message, true); // todo: configurable default message
        } else toast(message, false);
    }

    @Override
    protected void runWithData(@NonNull String message) {
        var longTag = string(R.string.tag_toast_long); // todo way later make configurable
        if (message.toLowerCase().endsWith(longTag)) {
            message = message.substring(0, message.length() - longTag.length()).trim();
            toast(message.isEmpty() ? string(R.string.toast_hello) : message, true); // todo: configurable default message
        } else toast(message, false);
    }

    @Override
    protected void runWithData(@NonNull String message, @NonNull String cont) {
        var longTag = string(R.string.tag_toast_long); // todo way later make configurable
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
