package net.emilla.command.core;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;
import net.emilla.settings.Aliases;

public class Notify extends CoreDataCommand {

    public static final String ENTRY = "notify";
    @StringRes
    public static final int NAME = R.string.command_notify;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_notify;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Notify::new, ENTRY, NAME, ALIASES);
    }

    private static class NotifyParams extends CoreDataParams {

        private NotifyParams() {
            super(NAME,
                  R.string.instruction_notify,
                  R.drawable.ic_notify,
                  R.string.summary_notify,
                  R.string.manual_notify,
                  R.string.data_hint_notify);
        }
    }

    public Notify(AssistActivity act) {
        super(act, new NotifyParams());
    }

    @Override
    protected void run() {
        throw new EmlaBadCommandException(NAME, R.string.error_unfinished_reminders); // Todo
    }

    @Override
    protected void run(String text) {
        throw new EmlaBadCommandException(NAME, R.string.error_unfinished_reminders); // Todo
    }

    @Override
    protected void runWithData(String data) {
        throw new EmlaBadCommandException(NAME, R.string.error_unfinished_reminders); // Todo
    }

    @Override
    protected void runWithData(String instruction, String data) {
        throw new EmlaBadCommandException(NAME, R.string.error_unfinished_reminders); // Todo
    }
}
