package net.emilla.commands;

import android.content.res.Resources;

import net.emilla.AssistActivity;
import net.emilla.R;

public class CommandToast extends CoreCommand implements DataCommand {
public CommandToast(final AssistActivity act) {
    super(act, R.string.command_toast, R.string.instruction_text);
}

@Override
public Command cmd() {
    return Command.TOAST;
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
