package net.emilla.commands;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;

public class AppSendDataCommand extends AppSendCommand implements DataCmd {
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

public AppSendDataCommand(AssistActivity act, String instruct, AppCmdInfo info,
        @StringRes int instructionId) {
    super(act, instruct, info, instructionId);
}

protected void runWithData(String message) {
    run(message);
}

protected void runWithData(String message, String cont) {
    run(message + '\n' + cont);
}

@Override
public void execute(String data) {
    if (mInstruction == null) runWithData(data);
    else runWithData(mInstruction, data);
}
}
