package net.emilla.command.core;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.settings.Aliases;

public final class Notify extends CoreDataCommand {

    public static final String ENTRY = "notify";
    @StringRes
    public static final int NAME = R.string.command_notify;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_notify;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Notify::new, ENTRY, NAME, ALIASES);
    }

    public Notify(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_notify,
              R.drawable.ic_notify,
              R.string.summary_notify,
              R.string.manual_notify,
              R.string.data_hint_notify);
    }

    @Override
    protected void run() {
        throw badCommand(R.string.error_unfinished_reminders); // Todo
    }

    @Override
    protected void run(@NonNull String text) {
        throw badCommand(R.string.error_unfinished_reminders); // Todo
    }

    @Override
    protected void runWithData(@NonNull String data) {
        throw badCommand(R.string.error_unfinished_reminders); // Todo
    }

    @Override
    protected void runWithData(@NonNull String instruction, @NonNull String data) {
        throw badCommand(R.string.error_unfinished_reminders); // Todo
    }
}
