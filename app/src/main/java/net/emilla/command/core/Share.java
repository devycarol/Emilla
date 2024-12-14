package net.emilla.command.core;

import static android.content.Intent.*;

import android.content.Intent;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Apps;

public class Share extends CoreDataCommand {
private final Intent mIntent = Apps.newTask(ACTION_SEND, "text/plain")
        .putExtra(EXTRA_TEXT, ""); // TODO: attachments
private final Intent mChooserIntent;

@Override @ArrayRes
public int detailsId() {
    return R.array.details_share;
}

@Override @StringRes
public int dataHint() {
    return R.string.data_hint_share;
}

@Override @DrawableRes
public int icon() {
    return R.drawable.ic_share;
}

public Share(AssistActivity act, String instruct) {
    super(act, instruct, R.string.command_share, R.string.instruction_app);
    mChooserIntent = createChooser(mIntent, string(R.string.dialog_app))
            .addFlags(FLAG_ACTIVITY_NEW_TASK);
    // This task appears to have special properties that are worth looking into...
}

@Override
protected void run() {
    succeed(mChooserIntent);
}

@Override
protected void run(String app) {
    runWithData(app); // TODO: allow to specify apps, conversation, and (ideally) people
}

@Override
protected void runWithData(String text) {
    // todo: ideally, query whether a given app supports newlines in advance and provide warning
    // TODO: this should be a pend rather than succeed, to allow the user to go back and
    //  reconsider
    String title = string(R.string.dialog_app);
    succeed(createChooser(mIntent.putExtra(EXTRA_TEXT, text), title)
            .addFlags(FLAG_ACTIVITY_NEW_TASK));
}

@Override
protected void runWithData(String app, String text) {
        runWithData(app + '\n' + text);
    }
}
