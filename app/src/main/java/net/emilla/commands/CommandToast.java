package net.emilla.commands;

import android.content.res.Resources;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;

public class CommandToast extends CoreDataCommand {
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

public CommandToast(final AssistActivity act) {
    super(act, R.string.command_toast, R.string.instruction_text);
}

@Override
public void run() { // todo: configurable default message
    give(resources().getString(R.string.toast_hello), false);
}

@Override
public void run(final String message) {
    final Resources res = resources();
    final String longTag = res.getString(R.string.tag_toast_long); // todo way later make configurable
    if (message.toLowerCase().endsWith(longTag)) {
        final String actualMessage = message.substring(0, message.length() - longTag.length()).trim();
        give(actualMessage.isEmpty() ? res.getString(R.string.toast_hello) : actualMessage, true); // todo: configurable default message
    } else give(message, false);
}

@Override
public void runWithData(final String message) {
    final Resources res = resources();
    final String longTag = res.getString(R.string.tag_toast_long); // todo way later make configurable
    if (message.toLowerCase().endsWith(longTag)) {
        final String actualMessage = message.substring(0, message.length() - longTag.length()).trim();
        give(actualMessage.isEmpty() ? res.getString(R.string.toast_hello) : actualMessage, true); // todo: configurable default message
    } else give(message, false);
}

@Override
public void runWithData(final String message, final String cont) {
    final String longTag = resources().getString(R.string.tag_toast_long); // todo way later make configurable
    if (message.toLowerCase().endsWith(longTag)) {
        final String actualMessage = message.substring(0, message.length() - longTag.length()).trim() + '\n' + cont;
        give(actualMessage, true);
    } else if (cont.toLowerCase().endsWith(longTag)) {
        final String actualMessage = message + '\n' + cont.substring(0, message.length() - longTag.length());
        give(actualMessage, true);
    }
    else give(message + '\n' + cont, false);
}
}
