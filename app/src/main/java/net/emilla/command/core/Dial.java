package net.emilla.command.core;

import static android.content.Intent.ACTION_DIAL;
import static android.net.Uri.parse;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.utils.Apps;

public class Dial extends CoreCommand {
private final Intent mIntent = Apps.newTask(ACTION_DIAL);

@Override @ArrayRes
public int detailsId() {
    return R.array.details_call_phone;
}

@Override @DrawableRes
public int icon() {
    return R.drawable.ic_dial;
}

@Override
public int imeAction() {
    return EditorInfo.IME_ACTION_GO;
}

public Dial(AssistActivity act, String instruct) {
    super(act, instruct, R.string.command_dial, R.string.instruction_dial);
}

@Override
protected void run() {
    if (packageManager().resolveActivity(mIntent, 0) == null) throw new EmlaAppsException("No dialer app found on your device."); // todo handle at mapping
    succeed(mIntent);
}

@Override
protected void run(String nameOrNumber) {
    if (packageManager().resolveActivity(mIntent, 0) == null) throw new EmlaAppsException("No dialer app found on your device."); // todo handle at mapping
    succeed(mIntent.setData(parse("tel:" + nameOrNumber)));
}
}
