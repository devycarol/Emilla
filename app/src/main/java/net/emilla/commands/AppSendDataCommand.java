package net.emilla.commands;

import android.content.Intent;

import androidx.annotation.StringRes;

import net.emilla.AssistActivity;

public class AppSendDataCommand extends AppSendCommand implements DataCommand {
public AppSendDataCommand(final AssistActivity act, final CharSequence label, final Intent launch,
        final Intent send, @StringRes final int instructionId) {
    super(act, label, launch, send, instructionId);
}

@Override
public Command cmd() {
    return Command.APP_SEND_DATA;
}

@Override
public void runWithData(final String message) {
    run(message);
}

@Override
public void runWithData(final String message, final String cont) {
    run(message + '\n' + cont);
}
}
