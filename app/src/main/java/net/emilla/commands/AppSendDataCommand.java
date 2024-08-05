package net.emilla.commands;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;

public class AppSendDataCommand extends AppSendCommand implements DataCommand {
@Override @ArrayRes
public int detailsId() {
    return R.array.details_app_send_data;
}

@Override
public boolean usesData() {
    return true;
}

@Override @StringRes
public int dataHint() {
    return R.string.data_hint_app_send_data;
}

@Override
public int imeAction() {
    return EditorInfo.IME_ACTION_NEXT;
}

public AppSendDataCommand(final AssistActivity act, final AppCmdInfo info,
        @StringRes final int instructionId) {
    super(act, info, instructionId);
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
