package net.emilla.commands;

import static android.content.Intent.*;

import android.content.Intent;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Apps;

public class CommandShare extends CoreCommand implements DataCommand {
private final Intent mIntent = Apps.newTask(ACTION_SEND, "text/plain")
        .putExtra(EXTRA_TEXT, ""); // TODO: attachments
private final Intent mChooserIntent;

public CommandShare(final AssistActivity act) {
    super(act, R.string.command_share, R.string.instruction_app);
    mChooserIntent = createChooser(mIntent, resources().getString(R.string.dialog_app))
            .addFlags(FLAG_ACTIVITY_NEW_TASK);
    // This task appears to have special properties that are worth looking into...
}

@Override
public Command cmd() {
    return Command.SHARE;
}

@Override
public void run() {
    succeed(mChooserIntent);
}

@Override
public void run(final String app) {
    runWithData(app); // TODO: allow to specify apps, conversation, and (ideally) people
}

@Override
public void runWithData(final String text) {
    // todo: ideally, query whether a given app supports newlines in advance and provide warning
    // TODO: this should be a pend rather than succeed, to allow the user to go back and
    //  reconsider
    final String title = resources().getString(R.string.dialog_app);
    succeed(createChooser(mIntent.putExtra(EXTRA_TEXT, text), title)
            .addFlags(FLAG_ACTIVITY_NEW_TASK));
}

@Override
public void runWithData(final String app, final String text) {
        runWithData(app + '\n' + text);
    }
}
