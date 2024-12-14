package net.emilla.command.core;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;

public class Toast extends CoreDataCommand {
@Override @ArrayRes
public int detailsId() {
    return R.array.details_toast;
}

@Override @StringRes
public int dataHint() {
    return R.string.data_hint_toast;
}

@Override @DrawableRes
public int icon() {
    return R.drawable.ic_toast;
}

public Toast(AssistActivity act, String instruct) {
    super(act, instruct, R.string.command_toast, R.string.instruction_text);
}

@Override
protected void run() { // todo: configurable default message
    give(string(R.string.toast_hello), false);
}

@Override
protected void run(String message) {
    String longTag = string(R.string.tag_toast_long); // todo way later make configurable
    if (message.toLowerCase().endsWith(longTag)) {
        String actualMessage = message.substring(0, message.length() - longTag.length()).trim();
        give(actualMessage.isEmpty() ? string(R.string.toast_hello) : actualMessage, true); // todo: configurable default message
    } else give(message, false);
}

@Override
protected void runWithData(String message) {
    String longTag = string(R.string.tag_toast_long); // todo way later make configurable
    if (message.toLowerCase().endsWith(longTag)) {
        String actualMessage = message.substring(0, message.length() - longTag.length()).trim();
        give(actualMessage.isEmpty() ? string(R.string.toast_hello) : actualMessage, true); // todo: configurable default message
    } else give(message, false);
}

@Override
protected void runWithData(String message, String cont) {
    String longTag = string(R.string.tag_toast_long); // todo way later make configurable
    if (message.toLowerCase().endsWith(longTag)) {
        String actualMessage = message.substring(0, message.length() - longTag.length()).trim() + '\n' + cont;
        give(actualMessage, true);
    } else if (cont.toLowerCase().endsWith(longTag)) {
        String actualMessage = message + '\n' + cont.substring(0, message.length() - longTag.length());
        give(actualMessage, true);
    }
    else give(message + '\n' + cont, false);
}
}
