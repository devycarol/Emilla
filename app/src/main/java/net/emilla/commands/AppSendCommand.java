package net.emilla.commands;

import static android.content.Intent.EXTRA_TEXT;

import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Lang;

public class AppSendCommand extends AppCommand {
@NonNull
private static CharSequence genericTitle(final Context ctxt, final CharSequence appLabel) {
    return Lang.colonConcat(ctxt.getResources(), R.string.command_app_send, appLabel);
}

@Override
public Command cmd() {
    return Command.APP_SEND;
}

@Override @ArrayRes
public int detailsId() {
    // Todo: this shouldn't apply to newpipes
    return R.array.details_app_send;
}

@Override
public int imeAction() {
    // Todo: this shouldn't apply when just launching and also not to the newpipes
    return EditorInfo.IME_ACTION_SEND;
}

private final Intent mSendIntent;

private AppSendCommand(final AssistActivity act, final CharSequence appLabel,
        final CharSequence cmdTitle, final Intent launch, final Intent send) {
    super(act, appLabel, cmdTitle, launch);
    mSendIntent = send;
}

public AppSendCommand(final AssistActivity act, final CharSequence appLabel, final Intent launch,
        final Intent send) {
    this(act, appLabel, genericTitle(act, appLabel), launch, send);
}

public AppSendCommand(final AssistActivity act, final CharSequence appLabel, final Intent launch,
        final Intent send, @StringRes final int instructionId) {
    this(act, appLabel, specificTitle(act, appLabel, instructionId), launch, send);
}

@Override
public void run(final String message) {
    // todo: instantly pull up bookmarked videos for newpipe
    succeed(mSendIntent.putExtra(EXTRA_TEXT, message));
}
}
