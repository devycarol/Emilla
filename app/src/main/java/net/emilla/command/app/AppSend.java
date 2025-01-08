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
import net.emilla.lang.Lang;
import net.emilla.utils.Apps;

public class AppSend extends AppCommand {

    @NonNull
    private static CharSequence genericTitle(Context ctxt, CharSequence appLabel) {
        return Lang.colonConcat(ctxt.getResources(), R.string.command_app_send, appLabel);
    }

    @Override @ArrayRes
    public int details() {
        // Todo: this shouldn't apply to newpipes
        return R.array.details_app_send;
    }

    @Override
    public int imeAction() {
        // Todo: this shouldn't apply when just launching and also not to the newpipes
        return EditorInfo.IME_ACTION_SEND;
    }

    private final Intent mSendIntent;

    private AppSend(AssistActivity act, String instruct, AppParams params, CharSequence cmdTitle) {
        super(act, instruct, params, cmdTitle);
        mSendIntent = Apps.sendToApp(params.pkg);
    }

    public AppSend(AssistActivity act, String instruct, AppParams params) {
        this(act, instruct, params, genericTitle(act, params.label));
    }

    public AppSend(AssistActivity act, String instruct, AppParams params,
            @StringRes int instructionId) {
        this(act, instruct, params, specificTitle(act, params.label, instructionId));
    }

    @Override
    protected void run(String message) {
        // todo: instantly pull up bookmarked videos for newpipe
        appSucceed(mSendIntent.putExtra(EXTRA_TEXT, message));
    }
}
