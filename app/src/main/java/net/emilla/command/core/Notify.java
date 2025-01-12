package net.emilla.command.core;

import androidx.annotation.ArrayRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;
import net.emilla.settings.Aliases;

public class Notify extends CoreDataCommand {

    public static final String ENTRY = "notify";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_notify;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    private static class NotifyParams extends CoreDataParams {

        private NotifyParams() {
            super(R.string.command_notify,
                  R.string.instruction_notify,
                  R.drawable.ic_notify,
                  R.string.summary_notify,
                  R.string.manual_notify,
                  R.string.data_hint_notify);
        }
    }

    public Notify(AssistActivity act, String instruct) {
        super(act, instruct, new NotifyParams());
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
