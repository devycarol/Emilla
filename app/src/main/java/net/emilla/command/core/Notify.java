package net.emilla.command.core;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;

public class Notify extends CoreDataCommand {

    public static final String ENTRY = "notify";

    public Notify(AssistActivity act, String instruct) {
        super(act, instruct, R.string.command_notify, R.string.instruction_notify);
    }

    @Override @StringRes
    public int dataHint() {
        return R.string.data_hint_notify;
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_notify;
    }

    @Override
    protected void run() {
        throw new EmlaBadCommandException(R.string.command_notify, R.string.error_unfinished_reminders); // Todo
    }

    @Override
    protected void run(String text) {
        throw new EmlaBadCommandException(R.string.command_notify, R.string.error_unfinished_reminders); // Todo
    }

    @Override
    protected void runWithData(String data) {
        throw new EmlaBadCommandException(R.string.command_notify, R.string.error_unfinished_reminders); // Todo
    }

    @Override
    protected void runWithData(String instruction, String data) {
        throw new EmlaBadCommandException(R.string.command_notify, R.string.error_unfinished_reminders); // Todo
    }
}
