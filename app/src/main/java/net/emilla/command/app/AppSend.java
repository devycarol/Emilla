package net.emilla.command.app;

import static android.content.Intent.EXTRA_TEXT;

import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Apps;
import net.emilla.utils.Lang;

public class AppSend extends AppCommand {
@NonNull
private static CharSequence genericTitle(Context ctxt, CharSequence appLabel) {
    return Lang.colonConcat(ctxt.getResources(), R.string.command_app_send, appLabel);
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

private AppSend(AssistActivity act, String instruct, AppCmdInfo info,
        CharSequence cmdTitle) {
    super(act, instruct, info, cmdTitle);
    mSendIntent = Apps.sendTask(info.pkg);
}

public AppSend(AssistActivity act, String instruct, AppCmdInfo info) {
    this(act, instruct, info, genericTitle(act, info.label));
}

public AppSend(AssistActivity act, String instruct, AppCmdInfo info,
        @StringRes int instructionId) {
    this(act, instruct, info, specificTitle(act, info.label, instructionId));
}

@Override
protected void run(String message) {
    // todo: instantly pull up bookmarked videos for newpipe
    succeed(mSendIntent.putExtra(EXTRA_TEXT, message));
}
}
